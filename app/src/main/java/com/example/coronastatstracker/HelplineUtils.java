package com.example.coronastatstracker;

public final class HelplineUtils {

    private static final Helpline[] Helplines = {new Helpline("India", "+911123978046"),
            new Helpline("Arunachal Pradesh", "9436055743"),
            new Helpline("Andhra Pradesh", "08662410978"),
            new Helpline("Assam", "6913347770"),
            new Helpline("Bihar", "104"),
            new Helpline("Chhattisgarh", "104"),
            new Helpline("Goa", "104"),
            new Helpline("Gujarat", "104"),
            new Helpline("Haryana", "8558893911"),
            new Helpline("Himachal Pradesh", "104"),
            new Helpline("Jharkhand", "104"),
            new Helpline("Karnataka", "104"),
            new Helpline("Kerala", "04712552056"),
            new Helpline("Madhya Pradesh", "104"),
            new Helpline("Maharashtra", "02026127394"),
            new Helpline("Manipur", "3852411668"),
            new Helpline("Meghalaya", "108"),
            new Helpline("Mizoram", "102"),
            new Helpline("Nagaland", "7005539653"),
            new Helpline("Odisha", "9439994859"),
            new Helpline("Punjab", "104"),
            new Helpline("Rajasthan", "01412225624"),
            new Helpline("Sikkim", "104"),
            new Helpline("Tamil Nadu", "04429510500"),
            new Helpline("Telangana", "104"),
            new Helpline("Tripura", "03812315879"),
            new Helpline("Uttarakhand", "104"),
            new Helpline("Uttar Pradesh", "18001805145"),
            new Helpline("West Bengal", "1800313444222"),
            new Helpline("Andaman and Nicobar Islands", "03192232102"),
            new Helpline("Chandigarh", "9779558282"),
            new Helpline("Dadra and Nagar Haveli", "104"),
            new Helpline("Daman and Diu", "104"),
            new Helpline("Delhi", "01122307145"),
            new Helpline("Jammu and Kashmir", "01912520982"),
            new Helpline("Ladakh", "01982256462"),
            new Helpline("Lakshadweep", "104"),
            new Helpline("Puducherry", "104")
    };

    public static String getFromArea(String area) {
        String hh = null;
        if (area == null || area.equals(""))
            return null;
        for (Helpline h : Helplines) {
            if (h.getArea().equalsIgnoreCase(area)) {
                hh = h.getNo();
                break;
            }
        }
        return hh;
    }

    private static class Helpline {
        private final String area;
        private final String no;

        Helpline(String area, String no) {
            this.area = area;
            this.no = no;
        }

        public String getArea() {
            return area;
        }

        public String getNo() {
            return no;
        }
    }
}


