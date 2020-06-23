# Minecraft Java Mods Optimizer

This application is a simple way to optimize Minecraft java mods size (and performance)  by up to 50 % !
***Examples:***

 - Advent Of Ascension: 85.9 MB -> 46.4 MB (-46%) !
 - Ore Spawn: 16.7 MB -> 7.6 MB (-54,5 %) !
 - Biomes O'Plenty: 3.1MB -> 2.0MB (-35 %) !



# What about the performance benefits ?
This tool lowers the requirements for playing Minecraft modpacks:

 - **Memory usage is lowered**, since files are smaller. It allows for faster startup times.
 - CPU usage when playing sounds, accessing images and sounds is **lowered**.
 - VRAM usage is **lowered** due to smaller file size. (If you are using integrated graphics, the RAM is used as VRAM)
 - **Increase on FPS on integrated graphics** due to lower RAM bandwidth being taken by textures.

# Should I use this awesome optimizer ?
**Yes**, I personally use this in **all my modpacks** to allow them **to run on my lower-end hardware** (limited RAM, integrated graphics...). 

However, consider performing other optimizations before doing this one, like removing useless mods, tweaking config files, using optifine and performing some windows 10 optimizations to make more RAM friendly (W7 is pretty RAM friendly already).

# Nice ! How is this achieved ?

Almost all textures within a Minecraft mods are at the .PNG format. I'm using the fantastic PNGQuant software to optimize all textures.

Same story for the sounds, all of them are stored at the .OGG format using the Vorbis codec. For this task, I'm using a custom build of FFMPEG to cut really high frequencies and save space.

Some more recent mods make heavy use of JSON files, those can be trimmed down as well.

# How to use the app ?

Upon opening the application, you will encounter the main (and only) menu of the app.

Notice that two options are already activated, as they are the recommended settings, however nothing blocks you from changing them.
Once your are satisfied with the options, click on the "+" button and choose a Minecraft mod to optimize.
After, just wait for the process to complete. 
You can see the progression of each steps with the various progress bars.


# Credits

 - StackOverflow for helping me to learn Java ^^'
 - [Kornelski](https://github.com/kornelski) for [PNGQuant](https://github.com/kornelski/pngquant) and [ndahlquist](https://github.com/ndahlquist) for the android [implementation](https://github.com/ndahlquist/pngquant-android)
 - [Tanersener](https://github.com/tanersener) for [mobile-ffmpeg](https://github.com/tanersener/mobile-ffmpeg)
 - [Charlesbihis](https://github.com/charlesbihis) for the [Json minifier](https://github.com/charlesbihis/minify) 
 - [SerpentSpirale](https://github.com/serpentspirale) for this app !

And of course, thanks to all contributors of all of those projects !

# Building the app
I'm developing **on Linux** *(lubuntu if you're curious)* so I don't know if it will work on Mac OS, and it **does not work on Windows**
I did not test it, but I'm fairly sure you can just clone the repository, open the android studio project on run the app, libs are pre-compiled.

## Things I should do that I surely won't.

 - Make the app compatible with API 21 instead of 24.
