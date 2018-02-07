package com.tripmate;

import android.app.models.CurrencyModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Sai Krishna on 9/2/2017.
 */

public class Utils {

    public static String DELIMITER_FOR_EXP_PERSONS = "%@#";
    public static String DELIMITER_FOR_EXP_PERSON_AND_AMOUNT = "$@^";
    public static String DELIMITER_FOR_PLACES_TO_VISIT = "$@#";

    public static String DELIMETER_FOR_TODOS ="$*^";
    public static String DELIMETER_FOR_A_TODO ="@+&";

    public static HashMap<Integer,Integer> themesHashMap = new  HashMap<>() ;
    public static HashMap<Integer,String> fontsHashMap = new  HashMap<>() ;
    public static HashMap<String,Integer> categoriesHashMap = new  HashMap<>() ;

    public static String currenciesListString = "{\n" +
            "    \"AED\": \"United Arab Emirates Dirham\",\n" +
            "    \"AFN\": \"Afghan Afghani\",\n" +
            "    \"ALL\": \"Albanian Lek\",\n" +
            "    \"AMD\": \"Armenian Dram\",\n" +
            "    \"ANG\": \"Netherlands Antillean Guilder\",\n" +
            "    \"AOA\": \"Angolan Kwanza\",\n" +
            "    \"ARS\": \"Argentine Peso\",\n" +
            "    \"AUD\": \"Australian Dollar\",\n" +
            "    \"AWG\": \"Aruban Florin\",\n" +
            "    \"AZN\": \"Azerbaijani Manat\",\n" +
            "    \"BAM\": \"Bosnia-Herzegovina Convertible Mark\",\n" +
            "    \"BBD\": \"Barbadian Dollar\",\n" +
            "    \"BDT\": \"Bangladeshi Taka\",\n" +
            "    \"BGN\": \"Bulgarian Lev\",\n" +
            "    \"BHD\": \"Bahraini Dinar\",\n" +
            "    \"BIF\": \"Burundian Franc\",\n" +
            "    \"BMD\": \"Bermudan Dollar\",\n" +
            "    \"BND\": \"Brunei Dollar\",\n" +
            "    \"BOB\": \"Bolivian Boliviano\",\n" +
            "    \"BRL\": \"Brazilian Real\",\n" +
            "    \"BSD\": \"Bahamian Dollar\",\n" +
            "    \"BTC\": \"Bitcoin\",\n" +
            "    \"BTN\": \"Bhutanese Ngultrum\",\n" +
            "    \"BWP\": \"Botswanan Pula\",\n" +
            "    \"BYN\": \"Belarusian Ruble\",\n" +
            "    \"BZD\": \"Belize Dollar\",\n" +
            "    \"CAD\": \"Canadian Dollar\",\n" +
            "    \"CDF\": \"Congolese Franc\",\n" +
            "    \"CHF\": \"Swiss Franc\",\n" +
            "    \"CLF\": \"Chilean Unit of Account (UF)\",\n" +
            "    \"CLP\": \"Chilean Peso\",\n" +
            "    \"CNH\": \"Chinese Yuan (Offshore)\",\n" +
            "    \"CNY\": \"Chinese Yuan\",\n" +
            "    \"COP\": \"Colombian Peso\",\n" +
            "    \"CRC\": \"Costa Rican Colón\",\n" +
            "    \"CUC\": \"Cuban Convertible Peso\",\n" +
            "    \"CUP\": \"Cuban Peso\",\n" +
            "    \"CVE\": \"Cape Verdean Escudo\",\n" +
            "    \"CZK\": \"Czech Republic Koruna\",\n" +
            "    \"DJF\": \"Djiboutian Franc\",\n" +
            "    \"DKK\": \"Danish Krone\",\n" +
            "    \"DOP\": \"Dominican Peso\",\n" +
            "    \"DZD\": \"Algerian Dinar\",\n" +
            "    \"EGP\": \"Egyptian Pound\",\n" +
            "    \"ERN\": \"Eritrean Nakfa\",\n" +
            "    \"ETB\": \"Ethiopian Birr\",\n" +
            "    \"EUR\": \"Euro\",\n" +
            "    \"FJD\": \"Fijian Dollar\",\n" +
            "    \"FKP\": \"Falkland Islands Pound\",\n" +
            "    \"GBP\": \"British Pound Sterling\",\n" +
            "    \"GEL\": \"Georgian Lari\",\n" +
            "    \"GGP\": \"Guernsey Pound\",\n" +
            "    \"GHS\": \"Ghanaian Cedi\",\n" +
            "    \"GIP\": \"Gibraltar Pound\",\n" +
            "    \"GMD\": \"Gambian Dalasi\",\n" +
            "    \"GNF\": \"Guinean Franc\",\n" +
            "    \"GTQ\": \"Guatemalan Quetzal\",\n" +
            "    \"GYD\": \"Guyanaese Dollar\",\n" +
            "    \"HKD\": \"Hong Kong Dollar\",\n" +
            "    \"HNL\": \"Honduran Lempira\",\n" +
            "    \"HRK\": \"Croatian Kuna\",\n" +
            "    \"HTG\": \"Haitian Gourde\",\n" +
            "    \"HUF\": \"Hungarian Forint\",\n" +
            "    \"IDR\": \"Indonesian Rupiah\",\n" +
            "    \"ILS\": \"Israeli New Sheqel\",\n" +
            "    \"IMP\": \"Manx pound\",\n" +
            "    \"INR\": \"Indian Rupee\",\n" +
            "    \"IQD\": \"Iraqi Dinar\",\n" +
            "    \"IRR\": \"Iranian Rial\",\n" +
            "    \"ISK\": \"Icelandic Króna\",\n" +
            "    \"JEP\": \"Jersey Pound\",\n" +
            "    \"JMD\": \"Jamaican Dollar\",\n" +
            "    \"JOD\": \"Jordanian Dinar\",\n" +
            "    \"JPY\": \"Japanese Yen\",\n" +
            "    \"KES\": \"Kenyan Shilling\",\n" +
            "    \"KGS\": \"Kyrgystani Som\",\n" +
            "    \"KHR\": \"Cambodian Riel\",\n" +
            "    \"KMF\": \"Comorian Franc\",\n" +
            "    \"KPW\": \"North Korean Won\",\n" +
            "    \"KRW\": \"South Korean Won\",\n" +
            "    \"KWD\": \"Kuwaiti Dinar\",\n" +
            "    \"KYD\": \"Cayman Islands Dollar\",\n" +
            "    \"KZT\": \"Kazakhstani Tenge\",\n" +
            "    \"LAK\": \"Laotian Kip\",\n" +
            "    \"LBP\": \"Lebanese Pound\",\n" +
            "    \"LKR\": \"Sri Lankan Rupee\",\n" +
            "    \"LRD\": \"Liberian Dollar\",\n" +
            "    \"LSL\": \"Lesotho Loti\",\n" +
            "    \"LYD\": \"Libyan Dinar\",\n" +
            "    \"MAD\": \"Moroccan Dirham\",\n" +
            "    \"MDL\": \"Moldovan Leu\",\n" +
            "    \"MGA\": \"Malagasy Ariary\",\n" +
            "    \"MKD\": \"Macedonian Denar\",\n" +
            "    \"MMK\": \"Myanma Kyat\",\n" +
            "    \"MNT\": \"Mongolian Tugrik\",\n" +
            "    \"MOP\": \"Macanese Pataca\",\n" +
            "    \"MRO\": \"Mauritanian Ouguiya (pre-2018)\",\n" +
            "    \"MRU\": \"Mauritanian Ouguiya\",\n" +
            "    \"MUR\": \"Mauritian Rupee\",\n" +
            "    \"MVR\": \"Maldivian Rufiyaa\",\n" +
            "    \"MWK\": \"Malawian Kwacha\",\n" +
            "    \"MXN\": \"Mexican Peso\",\n" +
            "    \"MYR\": \"Malaysian Ringgit\",\n" +
            "    \"MZN\": \"Mozambican Metical\",\n" +
            "    \"NAD\": \"Namibian Dollar\",\n" +
            "    \"NGN\": \"Nigerian Naira\",\n" +
            "    \"NIO\": \"Nicaraguan Córdoba\",\n" +
            "    \"NOK\": \"Norwegian Krone\",\n" +
            "    \"NPR\": \"Nepalese Rupee\",\n" +
            "    \"NZD\": \"New Zealand Dollar\",\n" +
            "    \"OMR\": \"Omani Rial\",\n" +
            "    \"PAB\": \"Panamanian Balboa\",\n" +
            "    \"PEN\": \"Peruvian Nuevo Sol\",\n" +
            "    \"PGK\": \"Papua New Guinean Kina\",\n" +
            "    \"PHP\": \"Philippine Peso\",\n" +
            "    \"PKR\": \"Pakistani Rupee\",\n" +
            "    \"PLN\": \"Polish Zloty\",\n" +
            "    \"PYG\": \"Paraguayan Guarani\",\n" +
            "    \"QAR\": \"Qatari Rial\",\n" +
            "    \"RON\": \"Romanian Leu\",\n" +
            "    \"RSD\": \"Serbian Dinar\",\n" +
            "    \"RUB\": \"Russian Ruble\",\n" +
            "    \"RWF\": \"Rwandan Franc\",\n" +
            "    \"SAR\": \"Saudi Riyal\",\n" +
            "    \"SBD\": \"Solomon Islands Dollar\",\n" +
            "    \"SCR\": \"Seychellois Rupee\",\n" +
            "    \"SDG\": \"Sudanese Pound\",\n" +
            "    \"SEK\": \"Swedish Krona\",\n" +
            "    \"SGD\": \"Singapore Dollar\",\n" +
            "    \"SHP\": \"Saint Helena Pound\",\n" +
            "    \"SLL\": \"Sierra Leonean Leone\",\n" +
            "    \"SOS\": \"Somali Shilling\",\n" +
            "    \"SRD\": \"Surinamese Dollar\",\n" +
            "    \"SSP\": \"South Sudanese Pound\",\n" +
            "    \"STD\": \"São Tomé and Príncipe Dobra (pre-2018)\",\n" +
            "    \"STN\": \"São Tomé and Príncipe Dobra\",\n" +
            "    \"SVC\": \"Salvadoran Colón\",\n" +
            "    \"SYP\": \"Syrian Pound\",\n" +
            "    \"SZL\": \"Swazi Lilangeni\",\n" +
            "    \"THB\": \"Thai Baht\",\n" +
            "    \"TJS\": \"Tajikistani Somoni\",\n" +
            "    \"TMT\": \"Turkmenistani Manat\",\n" +
            "    \"TND\": \"Tunisian Dinar\",\n" +
            "    \"TOP\": \"Tongan Pa'anga\",\n" +
            "    \"TRY\": \"Turkish Lira\",\n" +
            "    \"TTD\": \"Trinidad and Tobago Dollar\",\n" +
            "    \"TWD\": \"New Taiwan Dollar\",\n" +
            "    \"TZS\": \"Tanzanian Shilling\",\n" +
            "    \"UAH\": \"Ukrainian Hryvnia\",\n" +
            "    \"UGX\": \"Ugandan Shilling\",\n" +
            "    \"USD\": \"United States Dollar\",\n" +
            "    \"UYU\": \"Uruguayan Peso\",\n" +
            "    \"UZS\": \"Uzbekistan Som\",\n" +
            "    \"VEF\": \"Venezuelan Bolívar Fuerte\",\n" +
            "    \"VND\": \"Vietnamese Dong\",\n" +
            "    \"VUV\": \"Vanuatu Vatu\",\n" +
            "    \"WST\": \"Samoan Tala\",\n" +
            "    \"XAF\": \"CFA Franc BEAC\",\n" +
            "    \"XAG\": \"Silver Ounce\",\n" +
            "    \"XAU\": \"Gold Ounce\",\n" +
            "    \"XCD\": \"East Caribbean Dollar\",\n" +
            "    \"XDR\": \"Special Drawing Rights\",\n" +
            "    \"XOF\": \"CFA Franc BCEAO\",\n" +
            "    \"XPD\": \"Palladium Ounce\",\n" +
            "    \"XPF\": \"CFP Franc\",\n" +
            "    \"XPT\": \"Platinum Ounce\",\n" +
            "    \"YER\": \"Yemeni Rial\",\n" +
            "    \"ZAR\": \"South African Rand\",\n" +
            "    \"ZMW\": \"Zambian Kwacha\",\n" +
            "    \"ZWL\": \"Zimbabwean Dollar\"\n" +
            "}";


    public static HashMap<Integer, String> getFontsHashMap() {


//        fontsHashMap.put(1,"fonts/");


        return fontsHashMap;
    }


    public static HashMap<String, Integer> getCategoriesHashMap() {

        categoriesHashMap.put("Drink",R.drawable.cat_drinks);
        categoriesHashMap.put("Entertainment",R.drawable.cat_entertainment);
        categoriesHashMap.put("Food",R.drawable.cat_food);
        categoriesHashMap.put("Hotel",R.drawable.cat_hotel);
        categoriesHashMap.put("Medical",R.drawable.cat_medical);
        categoriesHashMap.put("Miscellaneous",R.drawable.cat_misc);
        categoriesHashMap.put("Parking",R.drawable.cat_parking);
        categoriesHashMap.put("Shopping",R.drawable.cat_shopping);
        categoriesHashMap.put("Toll",R.drawable.cat_toll);
        categoriesHashMap.put("Travel",R.drawable.cat_travel);
        categoriesHashMap.put("@#Add@#",R.drawable.cat_add);

        return categoriesHashMap;
    }

    public static ArrayList<CurrencyModel> getCurrienciesList(){
        ArrayList<CurrencyModel> currencyModelArrayList = new ArrayList<>();

        try {
            JSONObject currenciesListJson = new JSONObject(currenciesListString);
            Iterator keys = currenciesListJson.keys();

            while(keys.hasNext() ) {
                String key = (String) keys.next();
                 currencyModelArrayList.add(new CurrencyModel(currenciesListJson.getString(key),key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return currencyModelArrayList;
    }

    public static String getCorrespondingCurrencyName(String currencyCode){
        String currencyName ="";
        try {
            JSONObject currenciesListJson = new JSONObject(currenciesListString);
            Iterator keys = currenciesListJson.keys();

            while(keys.hasNext() ) {
                String key = (String) keys.next();
                if(key.equalsIgnoreCase(currencyCode)){
                    currencyName=currenciesListJson.getString(key);
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  currencyName;

    }


    public static HashMap<Integer, Integer> getThemesHashMap() {

        themesHashMap.put(1,R.style.AppTheme);
        themesHashMap.put(2,R.style.AppTheme2);
        themesHashMap.put(3,R.style.AppTheme3);
        themesHashMap.put(4,R.style.AppTheme4);
        themesHashMap.put(5,R.style.AppTheme5);
        themesHashMap.put(6,R.style.AppTheme6);
        themesHashMap.put(7,R.style.AppTheme7);
        themesHashMap.put(8,R.style.AppTheme8);
        themesHashMap.put(9,R.style.AppTheme9);
        themesHashMap.put(10,R.style.AppTheme10);
        themesHashMap.put(11,R.style.AppTheme11);
        themesHashMap.put(12,R.style.AppTheme12);
        themesHashMap.put(13,R.style.AppTheme13);
        themesHashMap.put(14,R.style.AppTheme14);
        themesHashMap.put(15,R.style.AppTheme15);
        themesHashMap.put(16,R.style.AppTheme16);
        themesHashMap.put(17,R.style.AppTheme17);
        themesHashMap.put(18,R.style.AppTheme18);

        return themesHashMap;
    }
}
