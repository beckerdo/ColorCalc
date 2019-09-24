# ColorCalc
A tool for calculating color values, sorting, grouping, and naming colors.

Using simple text files, one can provide a number of input colors and their names.
These colors can be matched to a number of dictionary colors. This tool will
group and match input colors to the dictionary colors.

The tool can create some 2D color plots and a 3D visualization of the colors on a
polar hue/saturation/luminance plot which is interactive and can be saved to an animated GIF file.

One use case is matching one paint set colors to other paint sets.

Example command line "java ColorCalc 
   -i ModelPaintSet1.txt,PaintSet2.txt
   -d BasicSaturatedColors.txt,BasicGrays.txt
   -o output.txt
   -col Name,RGB,HSL,S,Dict-Name,Dict-RGB,Dict-HSL
   -sort Dict-H--,Name
   -group S<<013 
   -rem "Colors grouped by major color, low saturation moved to end"
   -plot ColorPlot.png
   -visualize
   -visSteps 64"

The command will sort colors by basic hues (descending) and color name (ascending).
The low saturation colors (S<<013) are grouped to the end.
