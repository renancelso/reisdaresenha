package br.com.residaresenha.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.reisdaresenha.model.Liga;


@FacesConverter(value = "LigaConverter", forClass = Liga.class)
public class LigaConverter implements Converter {
	
	@Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
        if (arg2 != null && !"".equalsIgnoreCase(arg2)) {
        	Liga Liga = (Liga) arg1.getAttributes().get(arg2);
            return Liga;
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent arg1, Object arg2) {
        try {        	
            if (arg2 != null && !"".equals(arg2)) {
            	Liga entity = (Liga) arg2;
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
