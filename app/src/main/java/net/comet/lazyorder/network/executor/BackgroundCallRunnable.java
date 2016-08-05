package net.comet.lazyorder.network.executor;

public abstract class BackgroundCallRunnable<R> {

    public void preExecute() {}

    public abstract R runAsync();

    public void postExecute(R result) {}

 }
