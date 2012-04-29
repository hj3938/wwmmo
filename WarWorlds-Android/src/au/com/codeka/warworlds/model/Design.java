package au.com.codeka.warworlds.model;

import org.w3c.dom.Element;

import au.com.codeka.XmlIterator;

/**
 * This is the base "design" class which both \c ShipDesign and \c BuildingDesign inherit from.
 */
public class Design {
    protected String mID;
    protected String mName;
    protected String mDescription;
    protected int mBuildCost;
    protected int mBuildTimeSeconds;
    protected String mIconUrl;

    public String getID() {
        return mID;
    }
    public String getName() {
        return mName;
    }
    public String getDescription() {
        return mDescription;
    }
    public int getBuildCost() {
        return mBuildCost;
    }
    public int getBuildTimeSeconds() {
        return mBuildTimeSeconds;
    }
    public String getIconUrl() {
        return mIconUrl;
    }

    public abstract static class Factory {
        protected Element mDesignElement;

        public Factory(Element buildingElement) {
            mDesignElement = buildingElement;
        }

        protected void populateDesign(Design design) {
            design.mID = mDesignElement.getAttribute("id");

            for(Element elem : XmlIterator.childElements(mDesignElement)) {
                if (elem.getNodeName().equals("name")) {
                    design.mName = elem.getTextContent();
                } else if (elem.getNodeName().equals("description")) {
                    design.mDescription = elem.getTextContent();
                } else if (elem.getNodeName().equals("cost")) {
                    String value = elem.getAttribute("credits");
                    if (!value.equals("")) {
                        design.mBuildCost = Integer.parseInt(value);
                    }

                    value = elem.getAttribute("time");
                    if (!value.equals("")) {
                        double timeInHours = Double.parseDouble(value);
                        design.mBuildTimeSeconds = (int)(timeInHours * 3600);
                    }
                } else if (elem.getNodeName().equals("icon")) {
                    design.mIconUrl = elem.getTextContent();
                } else {
                    // ?? unknown element... ignore
                }
            }
        }
    }

}