package br.com.residaresenha.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.reisdaresenha.model.Premiacao;


@FacesConverter(value = "PremiacaoConverter", forClass = Premiacao.class)
public class PremiacaoConverter implements Converter {
	
	@Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
        if (arg2 != null && !"".equalsIgnoreCase(arg2)) {
        	Premiacao Premiacao = (Premiacao) arg1.getAttributes().get(arg2);
            return Premiacao;
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent arg1, Object arg2) {
        try {        	
            if (arg2 != null && !"".equals(arg2)) {
            	Premiacao entity = (Premiacao) arg2;
                String key = entity.getId().toString();
                arg1.getAttributes().put(key, entity);
                String codigo = entity.getId().toString();
                if (codigo != null) {
                    return codigo;
                }
            }            
            return (String) arg2;            
        } catch (Exception e) {
            return "";
        }
    }
}
