# Minecraft Java Mods Optimizer

This tool can optimize your Minecraft Java mod size (and performance) by up to 50%!

### Examples:

 - Advent Of Ascension: 85. 9MB -> 46.4 MB (-46%)!
 - Ore Spawn: 16.7 MB -> 7.6 MB (-54.5%)!
 - Biomes O'Plenty: 3.1 MB -> 2.0 MB (-35%)!



## Performance benefits
The android application lowers the requirements for playing Minecraft modpacks:

 - Smaller file sizes, resulting in **lower memory usage**, **lower VRAM usage**, and **faster startup times**.
 - **Lower CPU usage** when playing sounds and loading textures.


## Should I use this awesome optimizer?
**Yes!!** I personally use this in **all** my modpacks to allow them **to run on my lower-end hardware** (limited RAM, integrated graphics...).

However, you should consider some other optimizations before doing using this tool, such as:
 - Remove useless mods
 - Tweak config files
 - Use [performance-enhancing mods](https://github.com/NordicGamerFE/usefulmods)
 - Using the latest java for your minecraft version
 - And other miscellaneous Windows 10 Optimizations (you can find plenty of guides online)

## Nice! How does it work?
First, almost all textures in Minecraft Java mods use the `.PNG` format. This tool uses the fantastic PNGQuant software to optimize all textures.

Second, almost all sounds are stored at the `.OGG` format using the Vorbis codec. For these, the tool uses a custom build of FFMPEG to cut out really high frequencies and save space.

Finally, some more recent mods make heavy use of JSON files. These can be trimmed down as well.

## How to use the app?
Simply launch the app, select your mod(s), and click Optimize! The application will do everything for you.  
You'll be able to see the progression of each step with the various progress bars.

## How do I build the app myself?
I'm developing **on Linux** *(lubuntu if you're curious)* so I don't know if it will work on Mac OS, and it **does not work on Windows**.

<br />
<br />
<br />

Things I should do that I surely won't.

 - Make the app compatible with API 21 instead of 24.
<br />

## Credits

 - StackOverflow for helping me to learn Java ^^'
 - [Kornelski](https://github.com/kornelski) for [PNGQuant](https://github.com/kornelski/pngquant) and [ndahlquist](https://github.com/ndahlquist) for the android [implementation](https://github.com/ndahlquist/pngquant-android)
 - [Tanersener](https://github.com/tanersener) for [mobile-ffmpeg](https://github.com/tanersener/mobile-ffmpeg)
 - [Charlesbihis](https://github.com/charlesbihis) for the [Json minifier](https://github.com/charlesbihis/minify) 
 - [SylveonBottle](https://github.com/SylveonBottle) for proof-reading and updating the optimization list
 - [SerpentSpirale](https://github.com/serpentspirale) for this app!

And of course, thanks to all contributors of all of those projects!
