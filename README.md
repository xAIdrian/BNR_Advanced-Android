## Advanced Android by Big Nerd Ranch
https://training.bignerdranch.com/classes/advanced-android
<br>
###Table of Contents:
* [Networking Architecture](#arch)
* [Model Architecture](#model)
* [Sync Adapters](#sync)
* [Advanced View](#views)

### <a name="arch"></a>Networking Architecture

Download and search Venues from the Foursquare Api using Retrofit 1.9 and custom GSON deserialiazers.

Allow the user to check in venues with OAuth Authentication and Retrofit 1.9 posts.

Listen for background download completion using Observables in RxJava and RetroLambda.

Network Error Handling with Retrofit.

Unit and Integration Tests with Robolectric and Mockito.
<br>
###<a name="model"></a>Model Architecture (NerdMart)

Dependency Injection with Dagger 2.

RxJava Observables/Subscribers for View updates and Server responses.

DataBinding
<br>
###<a name="sync"></a>Sync Adapters (Twiter Sync Adapter)

Authenticator to intercept Retrofit network calls to process Twitter's Oauth authentication.

Content Provider to store downloaded data and bind to our listview.

Sync Adapter to constantly download new tweets and display them to the user.

Google Cloud Messaging to notify the user with a Push Notificaion when they have new tweets.
<br>
###<a name="views"></a> Advanced Views (NerdMail)

Navigation Drawer using Navigation Views.

Custom views to create a flattened view for a more efficient Recycler View.

Custom notification and mail notification template.

Integration testing with Espresso.
