package cz.followme.usecases;

import rx.Observer;
import rx.Subscription;

public interface Usecase<T> {

    Subscription execute(Observer<T> observer);
}
