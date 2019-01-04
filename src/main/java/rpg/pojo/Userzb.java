package rpg.pojo;

/**用户装备类
 * @author ljq
 *
 */
public class Userzb {
    private String id;

    private String username;

    private Integer zbid;

    private Integer njd;

    private Integer isuse;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public Integer getZbid() {
        return zbid;
    }

    public void setZbid(Integer zbid) {
        this.zbid = zbid;
    }

    public Integer getNjd() {
        return njd;
    }

    public void setNjd(Integer njd) {
        this.njd = njd;
    }

    public Integer getIsuse() {
        return isuse;
    }

    public void setIsuse(Integer isuse) {
        this.isuse = isuse;
    }
}