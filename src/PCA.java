import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;

import Jama.Matrix;

public class PCA {

	Matrix xMatrix = null;
	
	public Matrix xBar = null;
	public Matrix R = null;
	
	public Matrix matriceDeCovariance = null;
	
	Matrix principauxVecteurs = null;
	Matrix Z = null;

	

	public PCA (Matrix main)
	{
		this.xMatrix = main;
	}
	
	public void Calculate()
	{
		
		this.matriceDeCovariance = func.Fonctions.GenerateScatterMatrix(xMatrix);
		
		printToFile("Output/datMatrix.txt");
		
	}
	
	public void calculatePrincipauxVec(Matrix swap,Matrix matriceDeCov){
		int i = 0;
		
		while(i < swap.getRowDimension() && !getKPrincipauxVecteurs(i, swap))	{
			i++;			
		}
		i++;
		
		principauxVecteurs = vecteurPropre(matriceDeCov).getMatrix(0, vecteurPropre(matriceDeCov).getRowDimension()-1,
				vecteurPropre(matriceDeCov).getColumnDimension()-i, vecteurPropre(matriceDeCov).getColumnDimension()-1);
	
	//System.out.println("Vecteurs propres choisi: ");
	//principauxVecteurs.print(i, 8);
		
	}
	

	
	public Matrix vecteurPropre(Matrix matriceDeCovariance){		
		return matriceDeCovariance.eig().getV();	
	}
	
	public Matrix diagonal(Matrix matriceDeCovariance){
		return matriceDeCovariance.eig().getD();
	}
	
	public Matrix vecPTranspose(Matrix matriceDeCovariance){
		return vecteurPropre(matriceDeCovariance).transpose();
	}
	
	public Matrix getXbar(){
		return this.xBar;
	}
	
	public Matrix getXbarTranspose(){
		return this.xBar.transpose();
	}
	
	
	public Matrix getSwappedDIAGMatrix(Matrix start)
	{
		Matrix exit = new Matrix(start.getRowDimension(),start.getColumnDimension());
		
		Double[] sort = new Double[start.getRowDimension()];
		
		for(int i = 0; i < start.getRowDimension(); i++)
		{
			sort[i] = start.get(i,i);
		}
		Arrays.sort(sort, Collections.reverseOrder());
		
		for(int i = 0; i < start.getRowDimension(); i++)
		{
			exit.set(i, i, sort[i]);
		}
		
		return exit;
	}
	
	public boolean getKPrincipauxVecteurs(int k, Matrix from)
	{
		boolean decision = false;
		
		double num = 0;
		double denum = 0;
		
		for(int i = 0; i < from.getRowDimension(); i++)
		{
			if(i <= k)
			{
				num += from.get(i, i);
			}
			denum += from.get(i, i);
		}
		System.out.println("Pourcentage de alpha: " + (num/denum));
		decision = (num/denum >= 0.9) ? true : false;
		
		return decision;
	}
	
	public Matrix getSwapMatrix(Matrix swapping){
		Matrix t = new Matrix(swapping.getRowDimension(),swapping.getColumnDimension());
		
		int indexJ = 0;
		
		for (int j = swapping.getColumnDimension()-1; j >= 0;j--){	
			for(int i = 0; i < swapping.getRowDimension();i++){
				t.set(i, indexJ,
						swapping.get(i,j));
			}
			indexJ++;
		}
		return t;
	}
	
	public void printToFile(String path)
	{
		PrintWriter write = null;
		
		try
		{
			write = new PrintWriter(path, "UTF-8");
			
			write.append("Matrice de Covariance:");
			matriceDeCovariance.print(write, 5,7);
			
			write.append("Vecteur Propre:" );
			vecteurPropre(matriceDeCovariance).print(write, 5, 3);
			

			write.append("Diagonal: " );
			diagonal(matriceDeCovariance).print(write, 5, 8);
			getSwappedDIAGMatrix(diagonal(matriceDeCovariance)).print(write, 5, 8);
			
			write.append("Vecteur Propre Transpose:" );
			vecPTranspose(matriceDeCovariance).print(write, 5, 3);
			
			Matrix swapped = getSwappedDIAGMatrix(diagonal(matriceDeCovariance));
			
			calculatePrincipauxVec(swapped,matriceDeCovariance);
			
			// Do the shit for task 1-5: Projeter dans le sous espace de K Z= Xmoyenne*Vk
			
			//System.out.println("Matrice Z Projete");
			
			Z = getXbar().times(principauxVecteurs);
			
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
}
