package learn.reference.type;

/**
 * 卖家信息类
 *
 * 源码来自于《码处高效》，侵删
 *
 * @ClassName: SellerInfo
 * @author: Glorze
 * @since: 2020/3/16 21:47
 */
public class SellerInfo {

    /**
     * 姓名
     */
    private String name;

    /**
     * 房屋地址
     */
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
