package info.danbecker.colorcalc.demo;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
// import java.util.Iterator;

import javax.imageio.ImageIO;
// import javax.imageio.ImageReader;

/**
 * A demonstration of java.awt.color.ColorSpace classes.
 * 
 * Some example helpers:
 * <ul>
 * <li>https://www.programcreek.com/java-api-examples/?api=java.awt.color.ColorSpace
 * <li>Getting ICC_Profile from JPG: https://stackoverflow.com/questions/29707253/icc-based-image-conversion-using-java
 * <li>https://en.wikipedia.org/wiki/ICC_profile
 * </ul>
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 */
public class ColorSpaceDemo {
	public static final org.slf4j.Logger LOGGER = 
			org.slf4j.LoggerFactory.getLogger(ColorSpaceDemo.class);
	
	public static void main(String[] args) throws IOException {
        ColorSpaceDemo.predefinedClasses();
        
        // ColorSpaceDemo.jpgProfile( "src/test/resources/test.jpg" );
        // ColorSpaceDemo.jpgProfile( "src/test/resources/BlackPowderAWIContinentalArmyFront.jpg" );
        // ColorSpaceDemo.jpgProfile( "src/test/resources/Continentals.jpg" );
        ColorSpaceDemo.jpgProfile( "src/test/resources/Story6-800.jpg" );

        // String [] formats = ImageIO.getReaderMIMETypes();
        // String [] formats = ImageIO.getReaderFormatNames();
        String [] formats = ImageIO.getReaderFileSuffixes();
        for ( String format : formats ) {
            LOGGER.info( "   format=" + format);
        }

        // Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix("jpg");
        // while ( readers.hasNext() ) {
        //     ImageReader reader = readers.next();
        //     LOGGER.info( "   reader=" + reader.getFormatName());
        // }
	}
		
    public static final void predefinedClasses() {
        ColorSpaceDemo.logInfo( ColorSpace.CS_CIEXYZ );
        ColorSpaceDemo.logInfo( ColorSpace.CS_GRAY );
        ColorSpaceDemo.logInfo( ColorSpace.CS_LINEAR_RGB );
        ColorSpaceDemo.logInfo( ColorSpace.CS_PYCC);
        ColorSpaceDemo.logInfo( ColorSpace.CS_sRGB);
        // ColorSpaceDemo.logInfo( -1 ); // throws IllegalArgumentException        
    }

    
    public static final void jpgProfile( String fileName ) throws IOException {
        LOGGER.info( "File name=" + fileName ); 

        // ClassLoader classLoader = getClass().getClassLoader();
        // ClassLoader classLoader = ColorSpaceDemo.class.getClassLoader();
        // File file = new File(classLoader.getResource(fileName).getFile());
        // LOGGER.info( "file path=" + Path.of(fileName).toAbsolutePath().toString());
        File file = new File(Path.of(fileName).toAbsolutePath().toString() );
        
        BufferedImage image = ImageIO.read(file);
        ColorSpace cs = image.getColorModel().getColorSpace();
        logInfo( cs );
    }

    public static final void logInfo( int csInt ) {
        LOGGER.info( "csInt=" + csInt + ", name=" + ColorSpaceDemo.colorSpaceString( csInt )); 
        logInfo( ColorSpace.getInstance( csInt ) );
    }
    
    public static final void logInfo( ColorSpace cs ) {
        if ( null != cs ) {
            LOGGER.info( "   type=" + cs.getType() + ", " + ColorSpaceDemo.typeString( cs.getType() ));
            // LOGGER.info( "   is sRGB=" + cs.isCS_sRGB());
            StringBuilder componentString = new StringBuilder( "   numComponents=" + cs.getNumComponents()); 
            for ( int idx = 0; idx < cs.getNumComponents(); idx++ ) {
                componentString.append( ", " + cs.getName( idx ));
                componentString.append( "(range " + cs.getMinValue( idx ) + "-" + cs.getMaxValue( idx ) + ")");
            }
            LOGGER.info( componentString.toString()); 
            // LOGGER.info( "   is ICC_ColorSpace=" + (cs instanceof ICC_ColorSpace));
            if ( cs instanceof ICC_ColorSpace ) {
                ICC_ColorSpace iccCS = (ICC_ColorSpace) cs;
                ICC_Profile iccProfile = iccCS.getProfile();
                componentString = new StringBuilder( "   ICC_ColorSpace version=" + iccProfile.getMajorVersion() + "." + iccProfile.getMinorVersion());
                componentString.append( ", profileClass=" + iccProfileClassString(iccProfile.getProfileClass()) + ", pcsType=" + iccProfile.getPCSType());
                LOGGER.info( componentString.toString());
            }
        }
    }
    
    public static final String colorSpaceString( int csInt ) {
        switch ( csInt ) {
        case ColorSpace.CS_CIEXYZ: return "The CIEXYZ conversion color space defined above";
        case ColorSpace.CS_GRAY: return "The built-in linear gray scale color space";
        case ColorSpace.CS_LINEAR_RGB: return "A built-in linear RGB color space";
        case ColorSpace.CS_PYCC: return "The Photo YCC conversion color space";
        case ColorSpace.CS_sRGB: return "The sRGB color space defined at http://www.w3.org/pub/WWW/Graphics/Color/sRGB.html";        
        default: return "unknown ColorSpace int " + csInt;
        }
    }

    public static final String typeString( int csType ) {
        switch ( csType ) {
        case ColorSpace.TYPE_2CLR: return "Generic 2 component color";
        case ColorSpace.TYPE_3CLR: return "Generic 3 component color";
        case ColorSpace.TYPE_4CLR: return "Generic 4 component color";
        case ColorSpace.TYPE_5CLR: return "Generic 5 component color";
        case ColorSpace.TYPE_6CLR: return "Generic 6 component color";
        case ColorSpace.TYPE_7CLR: return "Generic 7 component color";
        case ColorSpace.TYPE_8CLR: return "Generic 8 component color";
        case ColorSpace.TYPE_9CLR: return "Generic 9 component color";
        case ColorSpace.TYPE_ACLR: return "Generic 10 component color";
        case ColorSpace.TYPE_BCLR: return "Generic 11 component color";
        case ColorSpace.TYPE_CCLR: return "Generic 12 component color";
        case ColorSpace.TYPE_DCLR: return "Generic 13 component color";
        case ColorSpace.TYPE_ECLR: return "Generic 14 component color";
        case ColorSpace.TYPE_FCLR: return "Generic 15 component color";
        case ColorSpace.TYPE_CMY: return "Family of CMY color spaces";
        case ColorSpace.TYPE_CMYK: return "Family of CMYK color spaces";
        case ColorSpace.TYPE_GRAY: return "Family of GRAY color spaces";
        case ColorSpace.TYPE_HLS: return "Family of HLS color spaces";
        case ColorSpace.TYPE_HSV: return "Family of HSV color spaces";
        case ColorSpace.TYPE_Lab: return "Family of Lab color spaces";
        case ColorSpace.TYPE_Luv: return "Family of Luv color spaces";
        case ColorSpace.TYPE_RGB: return "Family of RGB color spaces";
        case ColorSpace.TYPE_XYZ: return "Family of XYZ color spaces";
        case ColorSpace.TYPE_YCbCr: return "Family of YCbCr color spaces";
        case ColorSpace.TYPE_Yxy: return "Family of Yxy color spaces";     
        default: return "unknown ColorSpace type " + csType;
        }
    }
    
    public static final String iccProfileClassString( int pcInt ) {
        switch ( pcInt ) {
        case ColorSpace.CS_CIEXYZ: return "The CIEXYZ conversion color space defined above";
        case ICC_Profile.CLASS_ABSTRACT: return "Profile class is abstract";
        case ICC_Profile.CLASS_COLORSPACECONVERSION: return "Profile class is color space conversion";
        case ICC_Profile.CLASS_DEVICELINK: return "Profile class is device link";
        case ICC_Profile.CLASS_DISPLAY: return "Profile class is display";
        case ICC_Profile.CLASS_INPUT: return "Profile class is input";
        case ICC_Profile.CLASS_NAMEDCOLOR: return "Profile class is named color";
        case ICC_Profile.CLASS_OUTPUT: return "Profile class is output";     
        default: return "unknown ICCProfile class int " + pcInt;
        }
    }
}