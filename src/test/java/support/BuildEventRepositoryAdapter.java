package support;

import java.util.Collection;

import jenkins.plugins.htmlaudio.domain.BuildEvent;
import jenkins.plugins.htmlaudio.domain.BuildEventRepository;


public class BuildEventRepositoryAdapter implements BuildEventRepository {

    public void add(BuildEvent event) {
        throw new UnsupportedOperationException("not implemented");
    }

    public void remove(BuildEvent event) {
        throw new UnsupportedOperationException("not implemented");
    }

    public Collection<BuildEvent> list() {
        throw new UnsupportedOperationException("not implemented");
    }

    public Collection<BuildEvent> findNewerThan(long buildEventId) {
        throw new UnsupportedOperationException("not implemented");
    }

    public Long getLastEventId() {
        throw new UnsupportedOperationException("not implemented");
    }
}
