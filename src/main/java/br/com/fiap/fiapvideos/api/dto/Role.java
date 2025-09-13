package br.com.fiap.fiapvideos.api.dto;

public class Role {

    private String _id;
    private ERole name;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }

}
