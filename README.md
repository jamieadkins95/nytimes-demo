# nytimes-demo
Android project to reproduce [issue 304](https://github.com/NYTimes/Store/issues/304) in NYTimes Store.

## Expected Behaviour
When in airplane mode the Store should return any cached data, and then call onError due to a HostNotFoundException

## Current Behaviour
OnError is called immediately and no cached data is returned.

## Odd workaround
Adding a `doOnSuccess` call to the `Store.get()` gives us the expected behaviour. i.e. cached data and then the `onError` call
