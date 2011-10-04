package jenkins.plugins.htmlaudio.domain;

import static java.util.Arrays.asList;
import static jenkins.plugins.htmlaudio.domain.NotificationId.asNotificationId;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jenkins.plugins.htmlaudio.domain.NotificationRepository.NotificationRemover;
import jenkins.plugins.htmlaudio.domain.impl.VolatileNotificationRepositoryAndFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class VolatileNotificationRepoAndFactoryStressTest {
    
    private static final int NUM_CLIENTS = 200;
    private static final int NUM_NOTIFICATIONS_PER_CLIENT = 200;
    
    private final VolatileNotificationRepositoryAndFactory impl
        = new VolatileNotificationRepositoryAndFactory();
    private final NotificationRepository repo = impl;
    private final NotificationFactory factory = impl;
    
    private final CountDownLatch readyForAction = new CountDownLatch(NUM_CLIENTS);
    private final CountDownLatch start = new CountDownLatch(1);
    private final CountDownLatch completed = new CountDownLatch(NUM_CLIENTS);
    private final Set<String> completedClients = Collections.synchronizedSet(new HashSet<String>());
    
    
    @Test
    public void repo_maintains_integrity_during_onslaught_of_crazy_monkeys() {
        final long started = System.currentTimeMillis();
        
        runClients();
        
        // make sure that the expected number of notifications was created
        final Notification n = factory.createAndPersist("url", ""); 
        assertEquals((NUM_CLIENTS * NUM_NOTIFICATIONS_PER_CLIENT) + 1,
            n.getId().getValue());
        
        // and that the repo is still sane after the beating =)
        assertEquals(asList(n), repo.findNewerThan(asNotificationId(n.getId().getValue() - 1)));
        assertEquals(asList(n), repo.findNewerThan(null));
        
        System.out.println("completed in " + (System.currentTimeMillis() - started) + "ms");
    }


    private void runClients() {
        for (int i = 0; i < NUM_CLIENTS; i++) {
            new Client().start();
        }
        
        await(readyForAction, "readyForAction");
        start.countDown();
        await(completed, "completed");
        
        assertEquals("some clients failed", NUM_CLIENTS, completedClients.size());
    }
    
    
    private void await(CountDownLatch l, String latchName) {
        try {
            assertTrue("timed out while waiting for '" + latchName + "'",
                    l.await(30, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    private class Client extends Thread {
        
        private final Random rnd = new Random();
        private final List<NotificationId> myNotifications = new ArrayList<NotificationId>();
        private int remaining = NUM_NOTIFICATIONS_PER_CLIENT;
        
        public Client() {
            setDaemon(true);
        }
        
        @Override
        public void run() {
            readyForAction.countDown();
            try {
                log("ready...");
                await(start, "start");
                
                while (remaining > 0) {
                    createSome();
                    selectSome();
                    if (remaining % 2 == 0) {
                        removeSome();
                    }
                }
                removeAll();
                
                completedClients.add(Thread.currentThread().getName());
            } finally {
                completed.countDown();
            }
        }

        private void createSome() {
            final int createCount = rnd.nextInt(remaining + 1);
            
            log("creating " + createCount + " notifications" + ", " + remaining + " remaining");
            for (int i = 0; i < createCount; i++) {
                final Notification n = factory.createAndPersist("url", Thread.currentThread().getName());
                myNotifications.add(n.getId());
                remaining--;
            }
        }
        
        private void removeSome() {
            final int removeCount = rnd.nextInt(myNotifications.size() + 1);
            
            log("removing " + removeCount + " notifications" + ", "
                + myNotifications.size() + " currently in repo");
            
            repo.remove(new NotificationRemover() {
                public void remove(Iterator<Notification> notifications) {
                    int removed = 0;
                    
                    while (removed < removeCount
                            && notifications.hasNext()) {
                        final Notification n = notifications.next();
                        
                        if (myNotifications.contains(n.getId())) {
                            notifications.remove();
                            myNotifications.remove(n.getId());
                            removed++;
                        }
                    }
                    
                    assertEquals(removeCount, removed);
                }
            });
        }
        
        private void selectSome() {
            if (myNotifications.isEmpty()) {
                return;
            }
            
            final int selectFrom = rnd.nextInt(myNotifications.size());
            final int selectedCount = myNotifications.size() - selectFrom;
            
            log("selecting " + selectedCount + "/" + myNotifications.size() + " of my own notifications");
            
            int foundCount = 0;
            for (Notification n :
                    repo.findNewerThan(asNotificationId(myNotifications.get(selectFrom).getValue() - 1))) {
                final int index = myNotifications.indexOf(n.getId());
                
                if (index == -1) {
                    continue;
                }
                
                assertTrue(index >= selectFrom);
                foundCount++;
            }
            
            assertEquals(selectedCount, foundCount);
        }
        
        private void removeAll() {
            while (!myNotifications.isEmpty()) {
                removeSome();
            }
        }

        private void log(String message) {
            // System.out.println("Client#" + Thread.currentThread().getName() + ": " + message);
        }
    }
}
