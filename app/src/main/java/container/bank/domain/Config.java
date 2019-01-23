package container.bank.domain;

/**
 * Created by administrator on 09/10/17.
 */

public class Config extends Model  {

    public String colorPrimario;
    public String colorSecundario;
    public String bancoEscolhido ;


    public String getQueryParams() {
        return "bancoEscolhido="+this.bancoEscolhido+"&colorPrimario="+this.colorPrimario+"&colorSecundario="+this.colorSecundario ;
    }


}
