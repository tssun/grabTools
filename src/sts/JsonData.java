package sts;

import java.util.List;

/**
 * @author ts.Sun (2017/4/11).
 */
public class JsonData {
    private String retmsg;
    private List<String> flows;
    private String retcode;
    private String status;

    public String getRetmsg() {
        return retmsg;
    }

    public void setRetmsg(String retmsg) {
        this.retmsg = retmsg;
    }

    public String getRetcode() {
        return retcode;
    }

    public void setRetcode(String retcode) {
        this.retcode = retcode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getFlows() {
        return flows;
    }

    public void setFlows(List<String> flows) {
        this.flows = flows;
    }
}
