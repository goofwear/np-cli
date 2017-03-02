NewPipe CLI
===========

This is a small command line app demonstraiting how the [NewPipeExtractor](https://github.com/TeamNewPipe/NewPipeExtractor) works, and how it can be used for own purpouse.

The GetUrlNewPipe.jar is the already compiled and packed version of this app. Use it like this:
`java -jar GetUrlNewPipe.jar <youtube video url>`

This app will print the streamlink to the first 720p, 360p 320p video it finds. So it can be used to download a video via wget like this:
`wget $(java -jar GetNewPipe.jar <youtube video url>)`

Clone this repository via `git clone --recoursive https://github.com/TeamNewPipe/np-cli.git`
