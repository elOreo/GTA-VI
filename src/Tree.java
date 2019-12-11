public class Tree {
    private float[] brown = {0.9f, 0.7f, 0.5f};
    private float[] green = {0.2f, 0.8f, 0.2f};
    private ConeRenderer trunk = new ConeRenderer(0.2f, 0.3f, 1.1f, brown);
    private ConeRenderer treetop = new ConeRenderer(0.2f, 0.6f, 0.8f, green);

    public Tree(){

    }

    public ConeRenderer getTrunk(){
        return trunk;
    }

    public ConeRenderer getTreetop(){
        return treetop;
    }
}
