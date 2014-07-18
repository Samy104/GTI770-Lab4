package func;



import java.util.ArrayList;
import java.awt.image.*;
import java.io.*;
import java.util.Random;

import javax.imageio.ImageIO;

import Jama.Matrix;
/*
 * Functions for Matrices
 */
public class Fonctions {
	
	private Fonctions(){}
	
	public static Matrix getMatrixFromImage(BufferedImage img)
	{
		
		Matrix newImg = new Matrix(0, img.getHeight()*img.getWidth());
		
		for(int i=0;i<img.getHeight();i++) 
	    {
	        for(int j=0;j<img.getWidth();j++)
	        {
	        	int rgb = img.getRGB(i, j);
	        	System.out.print(rgb);
	        	
	            newImg.set(0,j,rgb);
	        }
	    }   
		
		return newImg;
	}
	
	public static Matrix PrepareMatrix(File files){
		Matrix collectionOfFiles = null;
		for(File file : files.listFiles())
		{
			if(file.isDirectory())
			{
				System.out.println("Directory: " + file.getName());
				if(collectionOfFiles == null)
				{
					collectionOfFiles = PrepareMatrix(file); // Calls same method again.
				}
				else
				{
					collectionOfFiles = AppendMatrix(collectionOfFiles, PrepareMatrix(file));
				}
			}
			else 
			{
				BufferedImage img = null;
				try {
				    	img = ImageIO.read(file);
				    	
				    	if(collectionOfFiles == null)
						{
							collectionOfFiles = getMatrixFromImage(img);
						}
						else
						{
							collectionOfFiles = AppendMatrix(collectionOfFiles, PrepareMatrix(file));
						}
				    	
				    	System.out.print(img.getData() +"\n");
				    	
				} catch (IOException e) {
					System.out.println("Could not read the file at " + file.getPath());
				} catch(NullPointerException e){
					System.out.print("File: "+file.getPath()+" is not an image. \n");
				}
				
				System.out.println("File: " + file.getName());
			}
		}
		return collectionOfFiles;
	}
	
	public static int getMaxDet(Matrix x)
	{
		double max = 0;
		int index = 0;
		
		for(int col = 0; col < x.getColumnDimension(); col++)
		{
			double det = x.getMatrix(0, x.getRowDimension()-1, col, col).det();
			index = (det > max) ? col : index;
			max = (det > max) ? det : max;
		}
		
		return index;
	}
	public static Matrix GenerateScatterMatrix(Matrix x){	
		
		Matrix xBar = new Matrix(x.getRowDimension(), x.getColumnDimension()-1);
		double average;
		for(int j = 0; j < 5;j++){
			average = mean(x,j+1);
			for(int i = 0; i < x.getRowDimension();i++){
				double meanTest = (x.get(i, j+1) - average);
				xBar.set(i, j,meanTest);
			}
		}
		
		return (xBar.transpose().times(xBar)).times(1.d/x.getRowDimension());
	}
	public static double mean(Matrix x, int column)
	{
		double data = 0;
		for(int i = 0; i < x.getRowDimension(); i++)
		{
			data += x.get(i, column);
		}
		
		return data/x.getRowDimension();
	}
	
	public static int GetAmount(Matrix matrix, int i) {
		int count = 0;
		for(int row = 0; row < matrix.getRowDimension(); row++)
		{
			count += (matrix.get(row, 0) == i) ? 1 : 0;
		}
		
		return count;
	}

	public static Matrix GetMatrixPowered(Matrix m, int power)
	{
		for(int i = 0; i <= power; i++)
		{
			m.times(m);
		}
		
		return m;
	}
	
	public static Matrix GetEPowered(Matrix m)
	{
		Matrix e = new Matrix(m.getRowDimension(),m.getColumnDimension());
		
		for(int i = 0; i < e.getRowDimension();i++)
		{
			for(int j = 0; j < e.getColumnDimension(); j++)
			{
				e.set(i, j, Math.pow(Math.E,m.get(i, j)));
			}
		}
		
		return e;
	}
	public static Matrix GetLogTen(Matrix m)
	{
		Matrix l = new Matrix(m.getRowDimension(),m.getColumnDimension());
		
		for(int i = 0; i < l.getRowDimension();i++)
		{
			for(int j = 0; j < l.getColumnDimension(); j++)
			{
				l.set(i, j, Math.log10(m.get(i, j)));
			}
		}
		
		return l;
	}
	
	public static Matrix AddToMatrix(Matrix m, double d)
	{
		Matrix a = new Matrix(m.getRowDimension(),m.getColumnDimension());
		
		for(int i = 0; i < a.getRowDimension();i++)
		{
			for(int j = 0; j < a.getColumnDimension(); j++)
			{
				a.set(i, j, (m.get(i, j) + d));
			}
		}
		
		return a;
	}
	
	public static Matrix AddToMatrix(Matrix m, int x)
	{
		Matrix a = new Matrix(m.getRowDimension(),m.getColumnDimension());
		
		for(int i = 0; i < a.getRowDimension();i++)
		{
			for(int j = 0; j < a.getColumnDimension(); j++)
			{
				a.set(i, j, (m.get(i, j) + x));
			}
		}
		
		return a;
	}
	
	public static Matrix RemoveToMatrix(Matrix m, int x)
	{
		Matrix a = new Matrix(m.getRowDimension(),m.getColumnDimension());
		
		for(int i = 0; i < a.getRowDimension();i++)
		{
			for(int j = 0; j < a.getColumnDimension(); j++)
			{
				a.set(i, j, (m.get(i, j) - x));
			}
		}
		
		return a;
	}
	
	public static Matrix RemoveToMatrix(Matrix m, double x)
	{
		Matrix a = new Matrix(m.getRowDimension(),m.getColumnDimension());
		
		for(int i = 0; i < a.getRowDimension();i++)
		{
			for(int j = 0; j < a.getColumnDimension(); j++)
			{
				a.set(i, j, (m.get(i, j) - x));
			}
		}
		
		return a;
	}
	
	public static Matrix DivideToMatrix(Matrix m, int x)
	{
		Matrix a = new Matrix(m.getRowDimension(),m.getColumnDimension());
		
		for(int i = 0; i < a.getRowDimension();i++)
		{
			for(int j = 0; j < a.getColumnDimension(); j++)
			{
				a.set(i, j, (m.get(i, j) / x));
			}
		}
		
		return a;
	}
	
	public static Matrix DivideToMatrix(Matrix m, double x)
	{
		Matrix a = new Matrix(m.getRowDimension(),m.getColumnDimension());
		
		for(int i = 0; i < a.getRowDimension();i++)
		{
			for(int j = 0; j < a.getColumnDimension(); j++)
			{
				a.set(i, j, (m.get(i, j) / x));
			}
		}
		
		return a;
	}
	/**
	 * Retourne une matrice aléatoirement choisi
	 * @author Samy Lemcelli
	 * 
	 * @param Matrice m
	 * @return Matrice aléatoire
	 */
	
	public static Matrix returnRandomizedMatrix(Matrix m) {
		
		Matrix randMatrix = new Matrix(m.getRowDimension(), m.getColumnDimension());
		ArrayList<Integer> remainingElements = new ArrayList<Integer>(m.getRowDimension());
		
		for(int i = 0; i < m.getRowDimension(); i++)
		{
			remainingElements.add(i);
		}
		int finalPosition = 0;
		while(!remainingElements.isEmpty())
		{
			int position = new Random().nextInt(remainingElements.size());
			int random = remainingElements.get(position);
			randMatrix.set(finalPosition,0,m.get(random, 0));
			randMatrix.set(finalPosition,1,m.get(random, 1));
			remainingElements.remove(position);
			finalPosition++;
		}
		return randMatrix;
	}
	
	/**
	 * La fonction prends le ArrayList de Matrices et choisis les 225 rangées qui n'appartiennent pas à l'indice.
	 * 
	 * @param ArrayList<Matrix> agg
	 * @param int thene
	 * @return Matrice aggregation
	 */
	public static Matrix aggregateExceptOne(ArrayList<Matrix> agg, int theone)
	{
		Matrix aggregation = new Matrix(225,2);
		int currentPos = 0;
		int[] col = {0,1};
		
		for(int i = 1; i < 10; i++)
		{
			if(theone != i)
			{
				aggregation.setMatrix(currentPos,(currentPos+24), col, agg.get(i));
				currentPos += 25;
			}
			
		}
		
		return aggregation;
	}
	
	public static Matrix AppendMatrix(Matrix a, Matrix b)
	{
		if(a.getRowDimension() != b.getRowDimension())
		{
			System.out.println("You can't augment two matrices that arn't compatible.");
			return null;
		}
		Matrix newMat = new Matrix(a.getRowDimension(), a.getColumnDimension()+b.getColumnDimension());
		
		// Merge the two matrices
		newMat.setMatrix(0, a.getRowDimension()-1, 0, a.getColumnDimension()-1, a);
		newMat.setMatrix(a.getRowDimension()-1,a.getRowDimension()-1+b.getRowDimension(),0 ,b.getColumnDimension(), b);
		
		return newMat;
	}
}
