# CN1Asciidoctor

Asciidoc to HTML converter for [Codename One](https://www.codenameone.com)

## Synopsis

This library provides support for converting Asciidoc to HTML inside Codename One apps.  It wraps [Asiidoctor.js](https://asciidoctor.org/docs/asciidoctor.js/) to provide this functionality.

## Platform Support

Should work in all platforms.  iOS, Android, Simulator, Javascript, UWP, and Desktop builds.

## Installation

Install the CN1Asciidoctor CN1lib through Codename One settings.

## Usage

~~~~
Asciidoctor doctor = new Asciidoctor();
String text = "= Hello World\n\nSome paragraph content";
ConvertOptions convertOptions = new ConvertOptions()
    .headerFooter(true);  // include header and footer in conversion
    
doctor.toHtml(text, convertOptions)
    .ready(html->{
            // html contains an HTML5 string of the contents.
    }
);
~~~~


For a full usage example see the [CN1AsciidoctorDemo](CN1AsciidoctorDemo) project.

## Credits

* cn1lib created by [Steve Hannah](https://sjhannah.com)
* [Asiidoctor.js](https://asciidoctor.org/docs/asciidoctor.js/)
* [Asciidoctor](https://asciidoctor.org/)

## License

MIT
