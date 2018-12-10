package rpg.pojo;

public class Gh {
    private Integer id;

    private String name;

    private Integer level;

    private String creatname;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getCreatname() {
        return creatname;
    }

    public void setCreatname(String creatname) {
        this.creatname = creatname == null ? null : creatname.trim();
    }
}