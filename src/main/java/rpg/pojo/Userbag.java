package rpg.pojo;

public class Userbag {
    private Integer id;

    private String username;

    private Integer gid;

    private Integer number;

    private Integer njd;

    private Integer isadd;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getNjd() {
        return njd;
    }

    public void setNjd(Integer njd) {
        this.njd = njd;
    }

    public Integer getIsadd() {
        return isadd;
    }

    public void setIsadd(Integer isadd) {
        this.isadd = isadd;
    }
}