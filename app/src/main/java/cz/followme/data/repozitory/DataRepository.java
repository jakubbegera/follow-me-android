package cz.followme.data.repozitory;


import cz.followme.data.model.responces.CreateSessionResponse;
import rx.Observable;

public interface DataRepository {

    Observable<CreateSessionResponse> createSessionObservable();

}
