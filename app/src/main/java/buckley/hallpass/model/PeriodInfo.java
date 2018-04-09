package buckley.hallpass.model;

/**
 * @author David Buckley
 * Updated 4/7/2018
 *
 * Contains all information related to a ClassPeriod.
 */
public class PeriodInfo {
    public static final String DEFAULT_SHEET = "Sheet1";

    private ClassPeriod period = null;
    private String periodStr = null;
    private String idGet = null;
    private String idGetSheet = null;
    private String idPost = null;
    private String idPostSheet = null;


    public PeriodInfo(ClassPeriod period, String periodStr) {
        this.period = period;
        this.periodStr = periodStr;
        setIdGetSheet(DEFAULT_SHEET);
        setIdPostSheet(DEFAULT_SHEET);
    }

    public ClassPeriod getPeriod() {
        return period;
    }
    public int getPeriodInt() {
        return period.ordinal();
    }
    public String getPeriodStr() {
        return periodStr;
    }
    public String getIdGet() {
        return idGet;
    }
    public String getIdGetSheet() { return idGetSheet; }
    public String getIdPost() {
        return idPost;
    }
    public String getIdPostSheet() { return idPostSheet; }

    @Override
    public String toString() {
        return periodStr;
    }

    public void setIdGet(String idGet) { this.idGet = idGet; }
    public void setIdGetSheet(String idGetSheet) { this.idGetSheet = idGetSheet; }
    public void setIdPost(String idPost) { this.idPost = idPost; }
    public void setIdPostSheet(String idPostSheet) { this.idPostSheet = idPostSheet; }

}
