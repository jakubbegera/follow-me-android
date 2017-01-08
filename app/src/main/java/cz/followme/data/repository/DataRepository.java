package cz.followme.data.repository;


import cz.followme.data.model.requests.PostLocationRequest;
import cz.followme.data.model.responces.CreateSessionResponse;
import cz.followme.data.model.responces.EmptyResponse;
import rx.Observable;

public interface DataRepository {

    Observable<CreateSessionResponse> createSessionObservable();

    Observable<EmptyResponse> createPostLocationObservable(PostLocationRequest requestData);

}
