package cz.followme.data.repository;


import cz.followme.data.model.responces.CreateSessionResponse;
import rx.Observable;

public interface DataRepository {

    Observable<CreateSessionResponse> createSessionObservable();

}
