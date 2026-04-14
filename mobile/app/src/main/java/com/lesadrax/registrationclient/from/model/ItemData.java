package com.lesadrax.registrationclient.from.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemData {
    private Type type;
    private String dataParent;
    private List<String> data;

    public ItemData(Type type, String dataParent, List<String> data) {
        this.type = type;
        this.dataParent = dataParent;
        this.data = data;
    }

    // Getters and setters
    public Type getType() {
        return type;
    }

    public String getDataParent() {
        return dataParent;
    }

    public List<String> getData() {
        return data;
    }

    public static List<String> get(Type type, String dataParent) {
        List<ItemData> list = getItems();
        return list.stream()
                .filter(item -> item.getType() == type && item.getDataParent().equals(dataParent))
                .findFirst()
                .map(ItemData::getData)
                .orElse(new ArrayList<>());
    }

    public static List<FormField.FormOption> getOptions(Type type, String dataParent){
        List<String> list = get(type, dataParent);
        List<FormField.FormOption> data = new ArrayList<>();

        int i = 0;
        for (String d:
                list) {
            i++;
            data.add(new FormField.FormOption(i, d));
        }

        return data;
    }

    public enum Type {
        REGION,
        PREFECTURE,
        COMMUNE,
        CANTON
    }

    public static void initData(){
        getCountries();
        getItems();
    }

    public static List<FormField.FormOption> countriesData = new ArrayList<>();
    public static List<FormField.FormOption> getCountries(){

        if (!countriesData.isEmpty())
            return countriesData;

        String[] countries = {
                "TOGO", "AFGHANISTAN", "AFRIQUE DU SUD", "ALBANIE", "ALGERIE", "ALLEMAGNE",
                "ANDORRE", "ANGOLA", "ANGUILLA", "ANTARCTIQUE", "ANTIGUA-ET-BARBUDA",
                "ANTILLES NEERLANDAISES", "ARABIE SAOUDITE", "ARGENTINE", "ARMENIE", "ARUBA",
                "AUSTRALIE", "AUTRICHE", "AZERBAIDJAN", "BAHAMAS", "BAHREIN", "BANGLADESH",
                "BARBADE", "BELARUS", "BELGIQUE", "BELIZE", "BENIN", "BERMUDES", "BHOUTAN",
                "BOLIVIE", "BOSNIE HERZEGOVINE", "BOTSWANA", "BOUVET (ILE)", "BRESIL",
                "BRUNEI", "BBULGARIE", "BURKINA FASO", "BURUNDI", "CAIMANES", "CAMBODGE",
                "CAMEROUN", "CANADA", "CAP-VERT", "CENTRAFRIQUE", "CEUTA", "CHILI", "CHINE",
                "CHRISTMAS", "CHYPRE", "COCOS", "COLOMBIE", "ILES COMORES", "CONGO", "RD CONGO",
                "COOK (Iles)", "COREE DU SUD", "COREE DU NORD", "COSTA RICA", "COTE D'IVOIRE",
                "CROATIE", "CUBA", "DANEMARK", "DJIBOUTI", "REPUBLIQUE DOMINICAINE",
                "DOMINIQUE", "EGYPTE", "EL SALVADOR", "EMIRATS ARABES UNIS", "EQUATEUR",
                "ERYTHREE", "Espagne", "ESTONIE", "ETATS-UNIS D'AMERIQUE", "ETHIOPIE",
                "ILES MALOUINES", "FEROE (Iles)", "FIDJI (Iles)", "FINLANDE", "France",
                "GABON", "GAMBIE", "GEORGIE", "GEORGIE DU SUD ET LES ILES SANDWICH DU SUD",
                "GHANA", "GIBRALTAR", "GRECE", "GRENADE", "GROENLAND", "GUADELOUPE",
                "GUAM", "GUATEMALA", "GUINEE", "GUINEE BISSAU", "GUINEE EQUATORIALE",
                "GUYANA", "GF", "HAITI", "HONDURAS", "HONG KONG", "HONGRIE",
                "ILE HEARD ET ILES MCDONALD", "ILES MINEURES", "ILES SALOMON", "INDE",
                "INDONESIE", "IRAN", "IRAQ", "Irlande", "ISLANDE", "ISRAEL", "ITAL",
                "JAMAIQUE", "JAPON", "JORDANIE", "KAZAKHSTAN", "KENYA", "KIRGHISTAN",
                "KIRIBATI", "KOSOVO", "KOWEIT", "LAOS", "LESOTHO", "LETTONIE", "LIBAN",
                "LIBERIA", "LIBYE", "LIECHTENSTEIN", "LITUANIE", "LUXEMBOURG", "MACAO",
                "MACEDOINE", "MADAGASCAR", "MALAISIE", "MALAWI", "MALDIVES", "MALI",
                "MALTE", "MARIANNES DU NORD", "MAROC", "MARSHALL", "MARTINIQUE", "MAURICE",
                "MAURITANIE", "MAYOTTE", "MELILLA", "MEXIQUE", "MICRONESIE", "MOLDAVIE",
                "MONGOLIE", "MONTENEGRO", "MONTSERRAT", "MOZAMBIQUE", "MYANMAR", "NAMIBIE",
                "NAURU", "NEPAL", "NICARAGUA", "NIGER", "NIGERIA", "NIUE", "NORFOLK",
                "NORVEGE", "NOUVELLE-CALEDONIE", "NOUVELLE-ZELANDE", "OMAN", "OUGANDA",
                "OUZBEKISTAN", "PAKISTAN", "PALAOS", "PANAMA", "PAPOUASIE-NELLE-GUINEE",
                "PARAGUAY", "PAYS-BAS", "PEROU", "PHILIPPINES", "PITCAIRN", "POLOGNE",
                "POLYNESIE FRANCAISE", "Portugal", "QATAR", "REUNION", "ROUMANIE",
                "Royaume-Uni", "RUSSIE", "RWANDA", "SAINT-BARTHELEMY", "SAINTE-HELENE",
                "SAINTE-LUCIE", "SAINT-KITTS-ET-NEVIS", "SAINT-MARIN", "SAINT-MARTIN",
                "SAINT-PIERRE-ET-MIQUELON", "SAINT-SIEGE", "SAINT-VINCENT-ET-LES GRENADINES",
                "SAMOA", "SAMOA AMERICAINES", "SAO TOME E PRINCIPE", "SENEGAL", "SERBIE",
                "SEYCHELLES ET DEPENDANCES", "SIERRA LEONE", "SINGAPOUR", "SLOVAQUIE",
                "SLOVENIE", "SOMALIE", "SOUDAN", "SRI LANKA", "SUEDE", "SUISSE", "SURINAME",
                "SWAZILAND", "SYRIE", "TADJIKISTAN", "TAIWAN", "TANZANIE", "TCHAD",
                "REPUBLIQUE TCHEQUE", "TERRES AUSTRALES FRANCAISES",
                "TERRITOIRE BRITANNIQUE DE L'OCEAN INDIEN", "TERRITOIRE PALESTINIEN OCCUPE",
                "THAILANDE", "TIMOR LESTE", "TOKELAU", "TONGA", "TRINIDAD ET TOBAGO",
                "TUNISIE", "TURKMENISTAN", "TURKS ET CAIQUES", "TURQUIE", "TUVALU",
                "UKRAINE", "URUGUAY", "VANUATU", "VENEZUELA", "VIERGES BRITANNIQUES",
                "VIERGES DES ETATS-UNIS", "VIET NAM", "WALLIS-ET-FUTUNA", "YEMEN",
                "ZAMBIE", "ZIMBABWE"
        };

        for (int i = 0; i < countries.length; i++) {
            countriesData.add(new FormField.FormOption(i+1, countries[i]));
        }

        return countriesData;
    }

    private static List<ItemData> items = new ArrayList<>();
    public static List<ItemData> getItems() {

        if (!items.isEmpty())
            return items;

        // Add Regions (they don't have a parent)
        items.add(new ItemData(Type.REGION, "TOGO", Arrays.asList("SAVANES", "KARA", "CENTRALE", "PLATEAUX", "MARITIME", "DAGL")));

        // Add Prefectures for each region
        items.add(new ItemData(Type.PREFECTURE, "CENTRALE", Arrays.asList("BLITTA", "MO", "SOTOUBOUA", "TCHAMBA", "TCHAOUDJO")));
        items.add(new ItemData(Type.PREFECTURE, "DAGL", Arrays.asList("GOLFE", "AGOE-NYIVE")));
        items.add(new ItemData(Type.PREFECTURE, "KARA", Arrays.asList("ASSOLI", "BASSAR", "BINAH", "DANKPEN", "DOUFELGOU", "KERAN", "KOZAH")));
        items.add(new ItemData(Type.PREFECTURE, "MARITIME", Arrays.asList("AVE", "BAS-MONO", "LACS", "VO", "YOTO", "ZIO")));
        items.add(new ItemData(Type.PREFECTURE, "PLATEAUX", Arrays.asList("AGOU", "AKEBOU", "AMOU", "ANIE", "DANYI", "EST-MONO", "HAHO", "KLOTO", "KPELE", "MOYEN-MONO", "OGOU", "WAWA")));
        items.add(new ItemData(Type.PREFECTURE, "SAVANES", Arrays.asList("CINKASSE", "KPENDJAL", "KPENDJAL OUEST", "OTI", "OTI SUD", "TANDJOARE", "TONE")));
        // Add Communes for each prefecture
        items.add(new ItemData(Type.COMMUNE, "AGOE-NYIVE", Arrays.asList("AGOE-NYIVE 1", "AGOE-NYIVE 2", "AGOE-NYIVE 3", "AGOE-NYIVE 4", "AGOE-NYIVE 5", "AGOE-NYIVE 6")));
        items.add(new ItemData(Type.COMMUNE, "AGOU", Arrays.asList("AGOU 1", "AGOU 2")));
        items.add(new ItemData(Type.COMMUNE, "AKEBOU", Arrays.asList("AKEBOU 1", "AKEBOU 2")));
        items.add(new ItemData(Type.COMMUNE, "AMOU", Arrays.asList("AMOU 1", "AMOU 2", "AMOU 3")));
        items.add(new ItemData(Type.COMMUNE, "ANIE", Arrays.asList("ANIE 2", "ANIE 1")));
        items.add(new ItemData(Type.COMMUNE, "ASSOLI", Arrays.asList("ASSOLI 2", "ASSOLI 1", "ASSOLI 3")));
        items.add(new ItemData(Type.COMMUNE, "AVE", Arrays.asList("AVE 2", "AVE 1")));
        items.add(new ItemData(Type.COMMUNE, "BAS-MONO", Arrays.asList("BAS-MONO 1", "BAS-MONO 2")));
        items.add(new ItemData(Type.COMMUNE, "BASSAR", Arrays.asList("BASSAR 1", "BASSAR 2", "BASSAR 3", "BASSAR 4")));
        items.add(new ItemData(Type.COMMUNE, "BINAH", Arrays.asList("BINAH 1", "BINAH 2")));
        items.add(new ItemData(Type.COMMUNE, "BLITTA", Arrays.asList("BLITTA 2", "BLITTA 3", "BLITTA 1")));
        items.add(new ItemData(Type.COMMUNE, "CINKASSE", Arrays.asList("CINKASSE 2", "CINKASSE 1")));
        items.add(new ItemData(Type.COMMUNE, "DANKPEN", Arrays.asList("DANKPEN 3", "DANKPEN 1", "DANKPEN 2")));
        items.add(new ItemData(Type.COMMUNE, "DANYI", Arrays.asList("DANYI 2", "DANYI 1")));
        items.add(new ItemData(Type.COMMUNE, "DOUFELGOU", Arrays.asList("DOUFELGOU 1", "DOUFELGOU 2", "DOUFELGOU 3")));
        items.add(new ItemData(Type.COMMUNE, "EST-MONO", Arrays.asList("EST-MONO 2", "EST-MONO 1", "EST-MONO 3")));
        items.add(new ItemData(Type.COMMUNE, "GOLFE", Arrays.asList("GOLFE 1", "GOLFE 2", "GOLFE 3", "GOLFE 4", "GOLFE 5", "GOLFE 6", "GOLFE 7")));
        items.add(new ItemData(Type.COMMUNE, "HAHO", Arrays.asList("HAHO 3", "HAHO 2", "HAHO 1", "HAHO 4")));
        items.add(new ItemData(Type.COMMUNE, "KERAN", Arrays.asList("KERAN 1", "KERAN 2", "KERAN 3")));
        items.add(new ItemData(Type.COMMUNE, "KLOTO", Arrays.asList("KLOTO 3", "KLOTO 1", "KLOTO 2")));
        items.add(new ItemData(Type.COMMUNE, "KOZAH", Arrays.asList("KOZAH 4", "KOZAH 3", "KOZAH 2", "KOZAH 1")));
        items.add(new ItemData(Type.COMMUNE, "KPELE", Arrays.asList("KPELE 1", "KPELE 2")));
        items.add(new ItemData(Type.COMMUNE, "KPENDJAL", Arrays.asList("KPENDJAL 2", "KPENDJAL 1")));
        items.add(new ItemData(Type.COMMUNE, "KPENDJAL OUEST", Arrays.asList("KPENDJAL-OUEST 1", "KPENDJAL-OUEST 2")));
        items.add(new ItemData(Type.COMMUNE, "LACS", Arrays.asList("LACS 3", "LACS 2", "LACS 1", "LACS 4")));
        items.add(new ItemData(Type.COMMUNE, "MO", Arrays.asList("MO 1", "MO 2")));
        items.add(new ItemData(Type.COMMUNE, "MOYEN-MONO", Arrays.asList("MOYEN-MONO 1", "MOYEN-MONO 2")));
        items.add(new ItemData(Type.COMMUNE, "OGOU", Arrays.asList("OGOU 2", "OGOU 1", "OGOU 3", "OGOU 4")));
        items.add(new ItemData(Type.COMMUNE, "OTI", Arrays.asList("OTI 2", "OTI 1")));
        items.add(new ItemData(Type.COMMUNE, "OTI SUD", Arrays.asList("OTI-SUD 1", "OTI-SUD 2")));
        items.add(new ItemData(Type.COMMUNE, "SOTOUBOUA", Arrays.asList("SOTOUBOUA 2", "SOTOUBOUA 3", "SOTOUBOUA 1")));
        items.add(new ItemData(Type.COMMUNE, "TANDJOARE", Arrays.asList("TANDJOARE 2", "TANDJOARE 1")));
        items.add(new ItemData(Type.COMMUNE, "TCHAMBA", Arrays.asList("TCHAMBA 1", "TCHAMBA 2", "TCHAMBA 3")));
        items.add(new ItemData(Type.COMMUNE, "TCHAOUDJO", Arrays.asList("TCHAOUDJO 4", "TCHAOUDJO 3", "TCHAOUDJO 1", "TCHAOUDJO 2")));
        items.add(new ItemData(Type.COMMUNE, "TONE", Arrays.asList("TONE 1", "TONE 4", "TONE 3", "TONE 2")));
        items.add(new ItemData(Type.COMMUNE, "VO", Arrays.asList("VO 4", "VO 2", "VO 3", "VO 1")));
        items.add(new ItemData(Type.COMMUNE, "WAWA", Arrays.asList("WAWA 1", "WAWA 3", "WAWA 2")));
        items.add(new ItemData(Type.COMMUNE, "YOTO", Arrays.asList("YOTO 2", "YOTO 1", "YOTO 3")));
        items.add(new ItemData(Type.COMMUNE, "ZIO", Arrays.asList("ZIO 1", "ZIO 3", "ZIO 2", "ZIO 4")));
        // Add canton for each commune
        items.add(new ItemData(Type.CANTON, "AGOE-NYIVE 1", Arrays.asList("AGOE-NYIVE=HOUMBI", "AGOE-NYIVE=ADOUIKO", "AGOE-NYIVE =ADJOUGBA", "AGOE-NYIVE=TOGOME", "AGOE-NYIVE=KELEGOUGAN DIGBLE", "AGOE-NYIVE=DJIGBLE", "AGOE-NYIVE=BOTOKOPE", "AGOE-NYIVE=NYAVIME AVEYIME", "AGOE-NYIVE=KITIDJAN", "AGOE-NYIVE=NYIVEME + APELEBUIME", "AGOE-NYIVE=TOTSI NYIVEME", "AGOE-NYIVE=TOTSI KPATEFI", "AGOE-NYIVE=TOTSI KLEVEGBLE", "AGOE-NYIVE=ANOMEGBLE", "AGOE-NYIVE=ANOME GBONVE", "AGOE-NYIVE=TELESSOU", "AGOE-NYIVE=TELESSOU ADOKPO KOPE", "AGOE-NYIVE=LOGOPE KPATEFI", "AGOE-NYIVE=HOUMBIGBLE", "AGOE-NYIVE=AHONGAKOPE ASSIYEYE", "AGOE-NYIVE=KLEVE", "AGOE-NYIVE=KPATEFI", "AGOE-NYIVE=ATSANVE", "AGOE-NYIVE=DEMAKPOE", "AGOE-NYIVE=APEGNIGBI", "AGOE-NYIVE=FIOVI", "AGOE-NYIVE=LOGOPE ATSANVE", "AGOE-NYIVE=LOGOPE", "AGOE-NYIVE=ANOKUI", "AGOE-NYIVE=ANOKUI NOGO", "AGOE-NYIVE=SOGBOSSITO", "AGOE-NYIVE=KOVE APELEBUIME", "AGOE-NYIVE=GNAMASSIGAN +ZOGBEGAN", "AGOE-NYIVE=SOGBOSSITO AZIALE KOPE")));
        items.add(new ItemData(Type.CANTON, "AGOE-NYIVE 2", Arrays.asList("LEGBASSITO=LEGBASSITO", "LEGBASSITO=AGOSSITO", "LEGBASSITO=AHONKPOE", "LEGBASSITO=YOHONOU", "LEGBASSITO=KOVE SIVAGNON KOPE", "LEGBASSITO=KPOKPLOVIME", "LEGBASSITO=DALIME", "LEGBASSITO=DALIKO", "LEGBASSITO=AMEDENTA AKI KOPE", "LEGBASSITO=AMADENTA ANAGLI KOPE", "LEGBASSITO=DOUTHE KOPE", "LEGBASSITO=ASSIKO", "LEGBASSITO=BOKPOKO", "LEGBASSITO=SILIVI KOPE ou ADIDOME", "LEGBASSITO=AVINATO", "LEGBASSITO=ATHIEME AHONKPOE", "LEGBASSITO=ATHIEME", "LEGBASSITO=MADJIKPETO", "LEGBASSITO=ZOVADJIN", "LEGBASSITO=KOVE AHONDJI KOPE")));
        items.add(new ItemData(Type.CANTON, "AGOE-NYIVE 3", Arrays.asList("VAKPOSSITO =ATSANVE", "VAKPOSSITO =AWOUDJA KOPE", "VAKPOSSITO =ELAVANYO ATSANVE", "VAKPOSSITO =ELAVANYO KLEVE", "VAKPOSSITO =HOSSOUKOPE")));
        items.add(new ItemData(Type.CANTON, "AGOE-NYIVE 4", Arrays.asList("TOGBLEKOPE=TOGBLE CENTRE", "TOGBLEKOPE=FIDOKPUI", "TOGBLEKOPE=GUENOU KOPE", "TOGBLEKOPE=DIKAME", "TOGBLEKOPE=BOKOR KOPE", "TOGBLEKOPE=KOTOKOLI ZONZO", "TOGBLEKOPE=HAOUSSA ZONGO", "TOGBLEKOPE=DJELEDZI", "TOGBLEKOPE=DEGOME", "TOGBLEKOPE=AKOIN", "TOGBLEKOPE=ATSANVE", "TOGBLEKOPE=AVEYIME", "TOGBLEKOPE=ALINKA", "TOGBLEKOPE=ALINKA NYIVEMEGBLE", "TOGBLEKOPE=TOWOUGANOU", "TOGBLEKOPE=KPEDEVI KOPE")));
        items.add(new ItemData(Type.CANTON, "AGOE-NYIVE 5", Arrays.asList("ZANGUERA=KOHE", "ZANGUERA=KLEME SANGUERA", "ZANGUERA=ATIGAN COPE", "ZANGUERA=DEKPO SANGUERA", "ZANGUERA=ANYIGBE", "ZANGUERA=VOGOME", "ZANGUERA=KOPEGAN", "ZANGUERA=AGBLELIKO", "ZANGUERA=AFIADEGNIGBA", "ZANGUERA=KLIKAME", "ZANGUERA=DANGBESSITO", "ZANGUERA=TAGA KOPE", "ZANGUERA=SANYRAKO", "ZANGUERA=EZION KOPE", "ZANGUERA=ZOPOMAHE", "ZANGUERA=NANEGBE", "ZANGUERA=NANEGBE ZOSSIME", "ZANGUERA=TSROKPOSSIME", "ZANGUERA=ASSIGOME")));
        items.add(new ItemData(Type.CANTON, "AGOE-NYIVE 6", Arrays.asList("ADETIKOPE=ADETIKOPE-CENTRE", "ADETIKOPE=AGNAVE", "ADETIKOPE=DEVIME", "ADETIKOPE=DZOVE", "ADETIKOPE=ADOGLOVE", "ADETIKOPE=LOMENYO KOPE", "ADETIKOPE=KPOKPOME-AGUTE", "ADETIKOPE=AGOTIME", "ADETIKOPE=KLADJEME", "ADETIKOPE=KPOTAVE", "ADETIKOPE=TONOUKOUTI", "ADETIKOPE=TSIKPLONOU-KONDJI")));
        items.add(new ItemData(Type.CANTON, "AGOU 1", Arrays.asList("ADJAHUN FIAGBE", "AGOU ATIGBE", "AGOU KEBO", "AGOU TAVIE", "AGOU YIBOE", "AGOU-AKPLOLO", "GADJA", "KATI", "NYOGBO-NORD (AGOU- NYOGBO AGBETIKO)", "NYOGBO-SUD (AGOU NYOGBO DZIDJOLE)")));
        items.add(new ItemData(Type.CANTON, "AGOU 2", Arrays.asList("ADZAKPA", "AGOTIME NORD", "AMOUSSOU KOPE")));
        items.add(new ItemData(Type.CANTON, "AKEBOU 1", Arrays.asList("DJON", "GBENDE", "KOUGNOHOU", "VHE", "YALLA")));
        items.add(new ItemData(Type.CANTON, "AKEBOU 2", Arrays.asList("KAMINA", "KPALAVE", "SEREGBENE")));
        items.add(new ItemData(Type.CANTON, "AMOU 1", Arrays.asList("ADIVA", "IMLE", "OUMA-AMLAME")));
        items.add(new ItemData(Type.CANTON, "AMOU 2", Arrays.asList("AMOU-OBLO", "EKPEGNON", "KPATEGAN", "SODO")));
        items.add(new ItemData(Type.CANTON, "AMOU 3", Arrays.asList("AVEDJE-ITADI", "EVOU", "GAME", "HIHEATRO", "OKPAHOE", "OTADI", "TEMEDJA")));
        items.add(new ItemData(Type.CANTON, "ANIE 1", Arrays.asList("ANIE", "KOLO-KOPE", "PALLAKOKO")));
        items.add(new ItemData(Type.CANTON, "ANIE 2", Arrays.asList("ADOGBENOU", "ATCHINEDJI", "GLITTO")));
        items.add(new ItemData(Type.CANTON, "ASSOLI 1", Arrays.asList("BAFILO", "BOULADE", "DAKO/DAOUDE")));
        items.add(new ItemData(Type.CANTON, "ASSOLI 2", Arrays.asList("ALEDJO", "KOUMONDE")));
        items.add(new ItemData(Type.CANTON, "ASSOLI 3", Arrays.asList("SOUDOU")));
        items.add(new ItemData(Type.CANTON, "AVE 1", Arrays.asList("ANDO", "ASSAHOUN", "DZOLO", "EDZI", "KEVE", "TOVEGAN")));
        items.add(new ItemData(Type.CANTON, "AVE 2", Arrays.asList("AKEPE", "BADJA", "NOEPE")));
        items.add(new ItemData(Type.CANTON, "BAS-MONO 1", Arrays.asList("AFAGNAGAN", "AFAGNAN", "AGOME-GLOZOU", "KPETSOU")));
        items.add(new ItemData(Type.CANTON, "BAS-MONO 2", Arrays.asList("AGBETIKO", "ATTITOGON", "HOMPOU")));
        items.add(new ItemData(Type.CANTON, "BASSAR 1", Arrays.asList("BAGHAN", "BASSAR", "KALANGA")));
        items.add(new ItemData(Type.CANTON, "BASSAR 2", Arrays.asList("BANGELI", "BITCHABE", "DIMORI")));
        items.add(new ItemData(Type.CANTON, "BASSAR 3", Arrays.asList("KABOU", "MANGA")));
        items.add(new ItemData(Type.CANTON, "BASSAR 4", Arrays.asList("SANDA-AFOHOU", "SANDA-KAGBANDA")));
        items.add(new ItemData(Type.CANTON, "BINAH 1", Arrays.asList("BOUFALE", "LAMA-DESSI", "PAGOUDA", "PESSARE", "PITIKITA", "SOLLA")));
        items.add(new ItemData(Type.CANTON, "BINAH 2", Arrays.asList("KEMERIDA", "KETAO", "SIRKA")));
        items.add(new ItemData(Type.CANTON, "BLITTA 1", Arrays.asList("BLITTA VILLAGE", "BLITTA-GARE", "DOUFOULI", "PAGALA-GARE", "TCHALOUDE", "WARAGNI", "YALOUMBE")));
        items.add(new ItemData(Type.CANTON, "BLITTA 2", Arrays.asList("AGBANDI", "KOFFITI", "LANGABOU", "TCHARE-BAOU")));
        items.add(new ItemData(Type.CANTON, "BLITTA 3", Arrays.asList("ATCHINTSE", "DIGUENGUE", "DIKPELEOU", "KATCHENKE", "M'POTI", "PAGALA-VILLAGE", "TCHIFAMA", "TINTCHRO", "WELLY", "YEGUE")));
        items.add(new ItemData(Type.CANTON, "CINKASSE 1", Arrays.asList("BOADE", "CINKASSE", "GNOAGA", "GOULOUNGOUSSI")));
        items.add(new ItemData(Type.CANTON, "CINKASSE 2", Arrays.asList("BIANKOURI", "NADJOUNDI", "SAM-NABA", "TIMBOU")));
        items.add(new ItemData(Type.CANTON, "DANKPEN 1", Arrays.asList("GUERIN-KOUKA", "KATCHAMBA", "KOULFIEKOU", "NAMPOCH")));
        items.add(new ItemData(Type.CANTON, "DANKPEN 2", Arrays.asList("KOUTCHITCHEOU", "NAMON", "NATCHIBORE", "NATCHITIKPI")));
        items.add(new ItemData(Type.CANTON, "DANKPEN 3", Arrays.asList("BAPURE", "KIDJABOUN", "NANDOUTA", "NAWARE")));
        items.add(new ItemData(Type.CANTON, "DANYI 1", Arrays.asList("DANYI-ELAVANYO", "DANYI-KAKPA", "YIKPA")));
        items.add(new ItemData(Type.CANTON, "DANYI 2", Arrays.asList("AHLON", "DANYI KPETO-EVITA", "DANYI-ATIGBA")));
        items.add(new ItemData(Type.CANTON, "DOUFELGOU 1", Arrays.asList("AGBANDE-YAKA", "BAGA", "KOKA", "MASSEDENA", "NIAMTOUGOU", "POUDA", "SIOU", "TENEGA")));
        items.add(new ItemData(Type.CANTON, "DOUFELGOU 2", Arrays.asList("ALLOUM", "KADJALLA", "LEON", "TCHORE")));
        items.add(new ItemData(Type.CANTON, "DOUFELGOU 3", Arrays.asList("ANIMA", "DEFALE", "KPAHA")));
        items.add(new ItemData(Type.CANTON, "EST-MONO 1", Arrays.asList("ELAVAGNON", "GBADJAHE")));
        items.add(new ItemData(Type.CANTON, "EST-MONO 2", Arrays.asList("BADIN", "KAMINA", "MORETAN")));
        items.add(new ItemData(Type.CANTON, "EST-MONO 3", Arrays.asList("KPESSI", "NYAMASSILA")));
        items.add(new ItemData(Type.CANTON, "GOLFE 1", Arrays.asList("BE=BE", "BE=GBENYEDJI", "BE=WETE", "BE=BE HEDJE", "BE=N'TIFAFA KOME", "BE=BE KPOTA", "BE=ANFAME", "BE=ATIEGOU", "BE=KELEGOUGAN NORD", "BE=KLOBATEME", "BE=ADAKPAME", "BE=KANYIKOPE", "BE=AKODESSEWA KPONOU", "BE=AKODESSEWA KPOTA", "BE=ZONE PORTUAIRE", "BE=AKODESSEWA", "BE=ABLOGAME", "BE=BE AHLIGO", "BE=KOTOKOU KONDJI", "BE=ANTONIO NETIME", "BE=SOUZA NETIME", "AMOUTIVE=BE KPEHENOU", "BE=BE APEYEME")));
        items.add(new ItemData(Type.CANTON, "GOLFE 2", Arrays.asList("BE=HEDZRANAWOE", "BE=KELEGOUGAN", "BE=TOKOIN AEROPORT", "BE=TOKOIN N'KAFU", "BE=SAINT JOSEPH", "BE=TOKOIN FOREVER", "BE=TOKOIN TAME", "BE=TOKOIN WUITI")));
        items.add(new ItemData(Type.CANTON, "GOLFE 3", Arrays.asList("BE=DOUMASSESSE", "BE=UNIVERSITE DE LOME", "BE=RESIDENCE DU BENIN", "BE=LOME II", "BE=GBONVIE", "BE=TOKOIN LYCEE", "BE=TOKOIN ELAVAGNON")));
        items.add(new ItemData(Type.CANTON, "GOLFE 4", Arrays.asList("AMOUTIVE=AMOUTIVE", "AMOUTIVE=BASSADJI", "AMOUTIVE=LOM NAVA", "AMOUTIVE=ABOBOKOME", "AMOUTIVE=AGUIAKOME", "AMOUTIVE=BENIGLATO", "AMOUTIVE=ADAWLATO", "AMOUTIVE=AGBADAHONOU", "AMOUTIVE=KOKETIME", "AMOUTIVE=SANGUERA", "AMOUTIVE=FREAU JARDIN", "AMOUTIVE=WETRIVI KONDJI", "AMOUTIVE=ADOBOUKOME", "AMOUTIVE=QUARTIER ADMINISTRATIF", "AMOUTIVE=KODJOVIAKOPE", "AMOUTIVE=NYEKONAKPOE", "AMOUTIVE=OCTAVIONO NETIME", "AMOUTIVE=HANOUKOPE", "AMOUTIVE=DOULASSAME", "AMOUTIVE=TOKOIN OUEST", "AMOUTIVE=TOKOIN HOPITAL", "AMOUTIVE=TOKOIN GBADAGO", "AFLAO GAKLI=TOKOIN SOLIDARITE", "BE=DOGBEAVOU", "AFLAO GAKLI=CASABLANCA", "AMOUTIVE=ABOVE", "AFLAO GAKLI=AKOSSOMBO", "AMOUTIVE=BE KLIKAME")));
        items.add(new ItemData(Type.CANTON, "GOLFE 5", Arrays.asList("AFLAO GAKLI=AFLAO GAKLI", "AFLAO GAKLI=AVENOU BATOME", "AFLAO GAKLI=SOVIEPE", "AFLAO GAKLI=TOTSI", "AFLAO GAKLI=AGBALEPEDOGAN", "AFLAO GAKLI=ANYIGBE", "AFLAO GAKLI=TESHIE", "AFLAO GAKLI=AMADAHOME", "AFLAO GAKLI=APEDOKOE", "AFLAO GAKLI=WESSOME", "AFLAO GAKLI=AVEDJI TELESSOU", "AFLAO GAKLI=ADIDOADIN")));
        items.add(new ItemData(Type.CANTON, "GOLFE 6", Arrays.asList("BAGUIDA=BAGUIDA", "BAGUIDA=ADAMAVO", "BAGUIDA=AVEPOZO", "BAGUIDA=DEVEGO", "BAGUIDA=AGODEKE", "BAGUIDA=KPOGAN")));
        items.add(new ItemData(Type.CANTON, "GOLFE 7", Arrays.asList("AFLAO-SAGBADO=SAGBADO", "AFLAO-SAGBADO=AGOTIME", "AFLAO-SAGBADO=LOGOTE", "AFLAO-SAGBADO=LANKOUVI SAKANI", "AFLAO-SAGBADO=AKATO VIEPE", "AFLAO-SAGBADO=AKATO AVOEME", "AFLAO-SAGBADO=AKATO DEME", "AFLAO-SAGBADO=SEGBE DOUANE", "AFLAO-SAGBADO=SEGBEGAN", "AFLAO-SAGBADO=SAGBADO ZANVI", "AFLAO-SAGBADO=KLEME YEWEPE", "AFLAO-SAGBADO=KLEME AGOKPANOU", "AFLAO-SAGBADO=WOUGOME", "AFLAO-SAGBADO=DEKPOR /WOUGOME DEKPOR", "AFLAO-SAGBADO=APEDOKOE GBOMAME", "AFLAO-SAGBADO=APEDOKOE AGOKPANOU", "AFLAO-SAGBADO=SAGBADO ASSIYEYE", "AFLAO-SAGBADO=WONYOME", "AFLAO-SAGBADO=YOKOE AGBLEGAN", "AFLAO-SAGBADO=ABLOGOME", "AFLAO-SAGBADO=AWATAME", "AFLAO-SAGBADO=GBLENKOMEGAN", "AFLAO-SAGBADO=YOKOE KOPEGAN", "AFLAO-SAGBADO=LANKOUVI")));
        items.add(new ItemData(Type.CANTON, "HAHO 1", Arrays.asList("ATCHAVE", "DALIA", "HAHOMEGBE", "NOTSE")));
        items.add(new ItemData(Type.CANTON, "HAHO 2", Arrays.asList("ASRAMA", "DJEMEGNI")));
        items.add(new ItemData(Type.CANTON, "HAHO 3", Arrays.asList("AKPAKPAKPE", "KPEDOME")));
        items.add(new ItemData(Type.CANTON, "HAHO 4", Arrays.asList("AYITO", "WAHALA")));
        items.add(new ItemData(Type.CANTON, "KERAN 1", Arrays.asList("AKPONTE", "KANDE", "PESSIDE")));
        items.add(new ItemData(Type.CANTON, "KERAN 2", Arrays.asList("ATALOTE", "HELOTA", "OSSACRE")));
        items.add(new ItemData(Type.CANTON, "KERAN 3", Arrays.asList("KOUTOUGOU", "NADOBA", "WARENGO")));
        items.add(new ItemData(Type.CANTON, "KLOTO 1", Arrays.asList("AGOME KPALIME", "GBALAVE", "HANYIGBA", "KPADAPE", "TOME", "TOVE", "WOME", "YOKELE")));
        items.add(new ItemData(Type.CANTON, "KLOTO 2", Arrays.asList("KPIME", "LAVIE", "LAVIE APEDOME")));
        items.add(new ItemData(Type.CANTON, "KLOTO 3", Arrays.asList("AGOME", "AGOME-TOMEGBE", "KOUMA")));
        items.add(new ItemData(Type.CANTON, "KOZAH 1", Arrays.asList("LAMA", "LANDA", "LASSA", "SOUMDINA")));
        items.add(new ItemData(Type.CANTON, "KOZAH 2", Arrays.asList("BOHOU", "KOUMEA", "PYA", "SARAKAWA", "TCHARE", "TCHITCHAO", "YADE")));
        items.add(new ItemData(Type.CANTON, "KOZAH 3", Arrays.asList("AWANDJELO", "KPINZINDE")));
        items.add(new ItemData(Type.CANTON, "KOZAH 4", Arrays.asList("ATCHANGBADE", "DJAMDE")));
        items.add(new ItemData(Type.CANTON, "KPELE 1", Arrays.asList("AKATA", "KPELE-DAWLOTOU", "KPELE-GOVIE", "KPELE-NOVIVE")));
        items.add(new ItemData(Type.CANTON, "KPELE 2", Arrays.asList("KPELE-CENTRE/GOUDEVE", "KPELE-DUTOE", "KPELE-GBALADZE", "KPELE-KAME", "KPELE-NORD")));
        items.add(new ItemData(Type.CANTON, "KPENDJAL 1", Arrays.asList("KOUNDJOARE", "MANDOURI", "TAMBIGOU")));
        items.add(new ItemData(Type.CANTON, "KPENDJAL 2", Arrays.asList("BORGOU")));
        items.add(new ItemData(Type.CANTON, "KPENDJAL-OUEST 1", Arrays.asList("NAKI-EST", "NAYEGA", "OGARO")));
        items.add(new ItemData(Type.CANTON, "KPENDJAL-OUEST 2", Arrays.asList("NAMOUNDJOGA", "PAPRI", "POGNO", "TAMBONGA")));
        items.add(new ItemData(Type.CANTON, "LACS 1", Arrays.asList("ANEHO", "GLIDJI")));
        items.add(new ItemData(Type.CANTON, "LACS 2", Arrays.asList("AGOUEGAN", "AKLAKOU")));
        items.add(new ItemData(Type.CANTON, "LACS 3", Arrays.asList("AGBODRAFO", "GBODJOME")));
        items.add(new ItemData(Type.CANTON, "LACS 4", Arrays.asList("ANFOIN", "FIATA", "GANAVE")));
        items.add(new ItemData(Type.CANTON, "MO 1", Arrays.asList("BOULOHOU", "DJARKPANGA", "KAGNIGBARA")));
        items.add(new ItemData(Type.CANTON, "MO 2", Arrays.asList("SAIBOUDE", "TINDJASSI")));
        items.add(new ItemData(Type.CANTON, "MOYEN-MONO 1", Arrays.asList("AHASSOME", "TADO", "TOHOUN")));
        items.add(new ItemData(Type.CANTON, "MOYEN-MONO 2", Arrays.asList("KATOME", "KPEKPLEME", "SALIGBE")));
        items.add(new ItemData(Type.CANTON, "OGOU 1", Arrays.asList("DJAMA", "GNAGNA", "WOUDOU", "WOUDOU")));
        items.add(new ItemData(Type.CANTON, "OGOU 2", Arrays.asList("AKPARE", "DATCHA", "KATORE")));
        items.add(new ItemData(Type.CANTON, "OGOU 3", Arrays.asList("GLEI")));
        items.add(new ItemData(Type.CANTON, "OGOU 4", Arrays.asList("OUNTIVOU")));
        items.add(new ItemData(Type.CANTON, "OTI 1", Arrays.asList("FARE", "MANGO", "SADORI")));
        items.add(new ItemData(Type.CANTON, "OTI 2", Arrays.asList("BARKOISSI", "GALANGASHIE", "LOKO", "NAGBENI", "TCHANAGA")));
        items.add(new ItemData(Type.CANTON, "OTI-SUD 1", Arrays.asList("GANDO", "MOGOU", "SAGBIEBOU", "TCHAMONGA")));
        items.add(new ItemData(Type.CANTON, "OTI-SUD 2", Arrays.asList("KOUMONGOU", "KOUNTOIRE", "NALI", "TAKPAMBA")));
        items.add(new ItemData(Type.CANTON, "SOTOUBOUA 1", Arrays.asList("KANIAMBOUA", "SOTOUBOUA", "TABINDE")));
        items.add(new ItemData(Type.CANTON, "SOTOUBOUA 2", Arrays.asList("ADJENGRE", "AOUDA", "FAZAO", "KERIADE", "SESSARO", "TITIGBE")));
        items.add(new ItemData(Type.CANTON, "SOTOUBOUA 3", Arrays.asList("BODJONDE", "KAZABOUA", "TCHEBEBE")));
        items.add(new ItemData(Type.CANTON, "TANDJOARE 1", Arrays.asList("BOGOU", "BOMBOUAKA", "BOULOGOU", "GOUNDOGA", "LOKO", "NANDOGA", "PLIGOU", "TAMONGUE")));
        items.add(new ItemData(Type.CANTON, "TANDJOARE 2", Arrays.asList("BAGOU", "DOUKPERGOU", "LOKPANO", "MAMPROUGOU", "NANO", "SANGOU", "SISSIAK", "TAMPIALIME")));
        items.add(new ItemData(Type.CANTON, "TCHAMBA 1", Arrays.asList("AFFEM", "ALIBI", "KRI-KRI (ADJEIDE)", "LARNI", "TCHAMBA")));
        items.add(new ItemData(Type.CANTON, "TCHAMBA 2", Arrays.asList("BAGO", "KOUSSOUNTOU")));
        items.add(new ItemData(Type.CANTON, "TCHAMBA 3", Arrays.asList("BALANKA", "GOUBI", "KABOLI")));
        items.add(new ItemData(Type.CANTON, "TCHAOUDJO 1", Arrays.asList("KADAMBARA", "KOMAH", "KPANGALAM", "KPARATAO", "TCHALO")));
        items.add(new ItemData(Type.CANTON, "TCHAOUDJO 2", Arrays.asList("LAMA-TESSI")));
        items.add(new ItemData(Type.CANTON, "TCHAOUDJO 3", Arrays.asList("ALEHERIDE", "AMAIDE", "KEMENI", "KOLINA")));
        items.add(new ItemData(Type.CANTON, "TCHAOUDJO 4", Arrays.asList("AGOULOU", "KPASSOUADE", "WASSARABO")));
        items.add(new ItemData(Type.CANTON, "TONE 1", Arrays.asList("BIDJENGA", "DAPAONG", "KOURIENTRE", "NATIGOU", "PANA", "POISSONGUI", "TOAGA")));
        items.add(new ItemData(Type.CANTON, "TONE 2", Arrays.asList("NAKI-OUEST", "NAMARE", "NANERGOU")));
        items.add(new ItemData(Type.CANTON, "TONE 3", Arrays.asList("LOTOGOU", "NIOUKPOURMA", "TAMI", "WARKAMBOU")));
        items.add(new ItemData(Type.CANTON, "TONE 4", Arrays.asList("KANTINDI", "KORBONGOU", "LOUANGA", "SANFATOUTE")));
        items.add(new ItemData(Type.CANTON, "VO 1", Arrays.asList("VOGAN", "VO-KOUTIME")));
        items.add(new ItemData(Type.CANTON, "VO 2", Arrays.asList("ANYRON KOPE", "TOGOVILLE")));
        items.add(new ItemData(Type.CANTON, "VO 3", Arrays.asList("DAGBATI", "DZREKPO", "MOME-HOUNKPATI")));
        items.add(new ItemData(Type.CANTON, "VO 4", Arrays.asList("AKOUMAPE", "HAHOTOE", "SEVAGAN")));
        items.add(new ItemData(Type.CANTON, "WAWA 1", Arrays.asList("BADOU", "KESSIBO", "KPETE BENA", "TOMEGBE")));
        items.add(new ItemData(Type.CANTON, "WAWA 2", Arrays.asList("EKETO", "GBANDI-N'KOUGNA", "GOBE")));
        items.add(new ItemData(Type.CANTON, "WAWA 3", Arrays.asList("DOUME", "KLABE EFOUKPA", "OKOU", "OUNABE", "ZOGBEGAN")));
        items.add(new ItemData(Type.CANTON, "YOTO 1", Arrays.asList("AMOUSSIME", "KINI-KONDJI", "KOUVE", "TABLIGBO")));
        items.add(new ItemData(Type.CANTON, "YOTO 2", Arrays.asList("AHEPE", "TCHEKPO", "ZAFI")));
        items.add(new ItemData(Type.CANTON, "YOTO 3", Arrays.asList("ESSE-GODJIN", "GBOTO", "SEDOME", "TOKPLI", "TOMETY-KONDJI")));
        items.add(new ItemData(Type.CANTON, "ZIO 1", Arrays.asList("ABOBO", "DALAVE", "DAVIE", "DJAGBLE", "GBATOPE", "GBLAINVIE", "KPOME", "TSEVIE")));
        items.add(new ItemData(Type.CANTON, "ZIO 2", Arrays.asList("BOLOU", "KOVIE", "MISSION-TOVE", "WLI")));
        items.add(new ItemData(Type.CANTON, "ZIO 3", Arrays.asList("AGBELOUVE", "GAME")));
        items.add(new ItemData(Type.CANTON, "ZIO 4", Arrays.asList("GAPE-CENTRE", "GAPE-KPODJI")));

        return items;
    }
}
