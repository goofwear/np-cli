# NewPipe CLI

This is a small command line app demonstrating how the [NewPipeExtractor](https://github.com/TeamNewPipe/NewPipeExtractor) works, and how it can be used for own purpose.

## Getting Started

First clone this repository with:

```
git clone --recursive https://github.com/TeamNewPipe/np-cli.git
```

Then you can compile it with:

```
./gradlew build
```

It'll create a `np-cli.zip` in `build/distributions`. Extract it somewhere and open it's `bin` folder. You can run it with:

```
java -jar np-cli.jar <URL>
```

It'll print the first 320p, 720p, or 360p video it finds. So you can download a video using `wget` with:

```
wget $(java -jar np-cli.jar <URL>)
```
