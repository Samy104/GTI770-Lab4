package func;

/**
 * to change
 */


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Scanner;
import func.*;
import javax.imageio.ImageIO;

import Jama.Matrix;

public class FileManager {
	// Convert PGM to Matrix
	public static Matrix convertPGMtoMatrix(String address, int picHeight, int picWidth) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(address);
		DataInputStream dis = new DataInputStream(fileInputStream);

		
		BufferedImage rend = new BufferedImage(picWidth, picHeight, BufferedImage.TYPE_BYTE_GRAY);
        
		
		// read the image data
		double[][] data2D = new double[picHeight][picWidth];
		for (int row = 0; row < picHeight && dis.available() != 0; row++) {
			for (int col = 0; col < picWidth && dis.available() != 0; col++) {
				
				data2D[row][col] = dis.readUnsignedByte();
				rend.setRGB(row, col, (int) data2D[row][col]);
			}
		}
		dis.close();
		ImageIO.write(rend, "png", new File("Output/image.png"));
		return new Matrix(data2D);
	}
	
	public static Matrix convertPGMtoMatrix(BufferedImage bi, int picHeight, int picWidth) throws IOException {
		
		Raster image_raster = bi.getData();
	     
        double[][] original; // where we'll put the image
               
        //get pixel by pixel
        int[] pixel = new int[1];
        int[] buffer = new int[1];
       
        // declaring the size of arrays
        original = new double[image_raster.getWidth()][image_raster.getHeight()];

        BufferedImage rend = new BufferedImage(image_raster.getWidth(), image_raster.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        //get the image in the array
       /* 
        for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
                Color color = new Color(bi.getRGB(x, y));
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                //red = green = blue = (int)(red * 0.299 + green * 0.587 + blue * 0.114);
                //color = new Color(red, green, blue);
                System.out.println(color.getRed() + " " + color.getGreen() + "  "+ color.getBlue());
                int rgb = color.getRGB();
                //System.out.println(rgb);
                original[x][y] = (int)(red * 0.299 + green * 0.587 + blue * 0.114);
                rend.setRGB(x, y, (int)(red * 0.299 + green * 0.587 + blue * 0.114));
            }
        }
        */
        double minDecimal = 0;
        int[] buffered = new int[1];
        for(int i = 0 ; i < image_raster.getWidth() ; i++)
            for(int j = 0 ; j < image_raster.getHeight() ; j++)
            {
            	Color col = new Color(bi.getRGB(i, j));
            	minDecimal = (minDecimal > col.getRGB() ? col.getRGB() : minDecimal);
                rend.setRGB(i, j, col.getRGB());
                original[i][j] = col.getRGB();
            }
        
        ImageIO.write(rend, "png", new File("Output/image.png"));
        Matrix total = func.Fonctions.DivideToMatrix(new Matrix(original), minDecimal);
        return total;                   
	}

	// Convert Matrix to PGM with numbers of row and column
	public static Matrix normalize(Matrix input){
		int row = input.getRowDimension();

		for(int i = 0; i < row; i ++){
			input.set(i, 0, 0-input.get(i, 0));

		}

		double max = input.get(0, 0);
		double min = input.get(0, 0);

		for(int i = 1; i < row; i ++){
			if(max < input.get(i,0))
				max = input.get(i, 0);

			if(min > input.get(i, 0))
				min = input.get(i, 0);

		}

		Matrix result = new Matrix(112,92);
		for(int p = 0; p < 92; p ++){
			for(int q = 0; q < 112; q ++){
				double value = input.get(p*112+q, 0);
				value = (value - min) *255 /(max - min);
				result.set(q, p, value);
			}
		}

		return result;

	}


	//convert matrices to images
	public static void convertMatricetoImage(Matrix x, int featureMode) throws IOException{
		int row = x.getRowDimension();
		int column = x.getColumnDimension();

		for(int i = 0; i < column; i ++){
			Matrix eigen = normalize(x.getMatrix(0, row-1, i, i));



			BufferedImage img = new BufferedImage(92,112,BufferedImage.TYPE_BYTE_GRAY);
			WritableRaster raster = img.getRaster();

			for(int m = 0; m < 112; m ++ ){
				for(int n = 0; n < 92; n ++){
					int value = (int)eigen.get(m, n);
					raster.setSample(n,m,0,value); 
				}
			}

			File file = null;
			if(featureMode == 0)
				file = new File("Eigenface"+i+".bmp");
			else if(featureMode == 1)
				file = new File("Fisherface"+i+".bmp");
			else if(featureMode == 2)
				file = new File("Laplacianface"+i+".bmp");

			if(!file.exists())
				file.createNewFile();

			ImageIO.write(img,"bmp",file);
		}

	}

	//convert single matrix to an image
	public static void convertToImage(Matrix input, int name) throws IOException{
		File file = new File("../gti770-lab4/imageRecreations/"+name+"_dimensions.png");
		if(!file.exists())
			file.createNewFile();

		BufferedImage img = new BufferedImage(92,112,BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = img.getRaster();

		for(int m = 0; m < 112; m ++ ){
			for(int n = 0; n < 92; n ++){
				int value = (int)input.get(n*112+m, 0);
				raster.setSample(n,m,0,value); 
			}
		}

		ImageIO.write(img,"png",file);

	}
	
	// Convert R, G, B, Alpha to standard 8 bit
    private static int colorToRGB(int alpha, int red, int green, int blue) {
 
        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red; newPixel = newPixel << 8;
        newPixel += green; newPixel = newPixel << 8;
        newPixel += blue;
 
        return newPixel;
 
    }
}