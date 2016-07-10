# Shortener

FreeCodeCamp API Exercise: URL Shortener

Run https://fcc-shortener.herokuapp.com/new/[YOUR_URL_HERE] to create short an address, you will receive the id.
And then you can distribute your shorten id here: https://fcc-shortener.herokuapp.com/[ID]

## Installation

Just run the below command to compile the project:
 
    $ lein uberjar

## Usage

You can run the server locally using the below command:
    
    $ java -cp target/shortener.jar clojure.main -m shortener.core
    
The default port is 8080, if you want to use other port just set the env. variable PORT.