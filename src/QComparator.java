import java.util.Comparator;

public class QComparator implements Comparator<QOffer> {
    @Override
    public int compare(QOffer x, QOffer y) {
        if (x.getQvalue() > y.getQvalue())
        {
            return -1;
        }
        if (x.getQvalue() < y.getQvalue())
        {
            return 1;
        }
        return 0;
    }
}
