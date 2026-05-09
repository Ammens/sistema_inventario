package modelo;

public class TipoProducto {
    private int idTipoProducto;
    private Integer idTipoPadre;
    private String descripcionTipo;
    private String estado;

    public TipoProducto() {}

    public TipoProducto(int idTipoProducto, Integer idTipoPadre, String descripcionTipo, String estado) {
        this.idTipoProducto = idTipoProducto;
        this.idTipoPadre = idTipoPadre;
        this.descripcionTipo = descripcionTipo;
        this.estado = estado;
    }

    public int getIdTipoProducto() { return idTipoProducto; }
    public void setIdTipoProducto(int idTipoProducto) { this.idTipoProducto = idTipoProducto; }

    public Integer getIdTipoPadre() { return idTipoPadre; }
    public void setIdTipoPadre(Integer idTipoPadre) { this.idTipoPadre = idTipoPadre; }

    public String getDescripcionTipo() { return descripcionTipo; }
    public void setDescripcionTipo(String descripcionTipo) { this.descripcionTipo = descripcionTipo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() { return descripcionTipo; }
}
