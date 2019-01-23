package container.bank.domain;
import android.graphics.Bitmap;


public class Contact extends Model {
	
 	public String nome ;
	public String  numeroTelefonico ;

	public String photoStringContact ;
	public Bitmap photoContact ;
	
	
	
	public Contact(){}

	public Contact(String nome, String numeroTelefonico) {
		this.nome = nome;
		this.numeroTelefonico = numeroTelefonico;
	}
}
