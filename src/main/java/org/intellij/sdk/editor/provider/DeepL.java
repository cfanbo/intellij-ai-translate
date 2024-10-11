package org.intellij.sdk.editor.provider;

import com.deepl.api.DeepLException;
import com.deepl.api.Language;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import org.intellij.sdk.editor.ConfigurationException;
import org.intellij.sdk.editor.LLmService;
import org.intellij.sdk.editor.settings.AppSettings;
import org.intellij.sdk.editor.util.Helper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeepL extends Base implements LLmService {
    Translator translator;

    String commonLangPairs = """
            "Simplified Chinese": 'zh',
            "English": '',
            "Japanese": 'ja',
            "Korean": 'ko',
            "Spanish": 'es',
            "German": 'de',
            "French": 'fr',
            "Russian": 'ru',
            "Arabic": 'ar',
            "Italian": 'it',
            "Malay": '',
            "Indonesian": 'id',
            "Vietnamese": '',
            "Afrikaans": '',
            "Thai": '',
            "Urdu": '',
            "Cantonese (Traditional)": '',
            "Northeastern Chinese": '',
            "Tibetan": '',
            "Classical Chinese": '',
            "Amharic": '',
            "Azerbaijani": '',
            "Belarusian": '',
            "Bulgarian": 'bg',
            "Bengali": '',
            "Bosnian": '',
            "Catalan": '',
            "Cebuano": '',
            "Corsican": '',
            "Czech": 'cs',
            "Welsh": '',
            "Danish": 'da',
            "Greek": 'el',
            "Esperanto": '',
            "Estonian": 'et',
            "Basque": '',
            "Persian": '',
            "Finnish": 'fi',
            "Filipino": '',
            "Fijian": '',
            "Frisian": '',
            "Irish": '',
            "Scottish Gaelic": '',
            "Galician": '',
            "Gujarati": '',
            "Hausa": '',
            "Hawaiian": '',
            "Hebrew": '',
            "Hindi": '',
            "Hmong": '',
            "Croatian": '',
            "Haitian Creole": '',
            "Hungarian": 'hu',
            "Armenian": '',
            "Igbo": '',
            "Icelandic": '',
            "Javanese": '',
            "Georgian": '',
            "Kazakh": '',
            "Khmer": '',
            "Kannada": '',
            "Kurdish": '',
            "Kyrgyz": '',
            "Latin": '',
            "Luxembourgish": '',
            "Lao": '',
            "Lithuanian": 'lt',
            "Latvian": 'lv',
            "Malagasy": '',
            "Maori": '',
            "Macedonian": '',
            "Malayalam": '',
            "Mongolian": '',
            "Marathi": '',
            "Maltese": '',
            "Burmese": '',
            "Dutch": 'nl',
            "Punjabi": '',
            "Polish": 'pl',
            "Pashto": '',
            "Romanian": 'ro',
            "Sanskrit": '',
            "Sinhala": '',
            "Slovak": 'sk',
            "Slovenian": 'sl',
            "Samoan": '',
            "Shona": '',
            "Somali": '',
            "Albanian": '',
            "Serbian": '',
            "Serbian (Cyrillic)": '',
            "Serbian (Latin)": '',
            "Sesotho": '',
            "Sundanese": '',
            "Swedish": 'sv',
            "Swahili": '',
            "Tamil": '',
            "Telugu": '',
            "Tajik": '',
            "Turkish": 'tr',
            "Uyghur": '',
            "Ukrainian": 'uk',
            "Uzbek": '',
            "Xhosa": '',
            "Yiddish": '',
            "Yoruba": '',
            "Zulu": '',
            "Roman Urdu": ''
            """;


    public DeepL(AppSettings.State conf) throws ConfigurationException, IOException {
        super(conf);

        translator = new Translator(this.baseConfig.apiKey);
    }

    public void callAgentApp(String text) throws Exception {
        String toLanguage = this.getTargetLang();
        if (toLanguage.isEmpty()) {
            throw new Exception("[DeepL] Target language Unsupported");
        }
        TextResult result = translator.translateText(text, null, toLanguage);
        Helper.printToConsole(result.getText());
        Helper.printFinished();
    }

    private String getSourceLang() throws DeepLException, InterruptedException, IOException {
        String langCode;
        langCode = this.getLangCode("source");
        if (!langCode.isEmpty()) {
            return langCode;
        }


        Map<String, String> langMap = new HashMap<>();
        langMap.putAll(this.getCommonLangMap());
        // add custom kv pairs

        if (langMap.containsKey(this.targetLanguage)) {
            return langMap.get(this.targetLanguage);
        }

        return "";
    }

    private String getTargetLang() throws DeepLException, IOException, InterruptedException {
        String langCode;
        langCode = this.getLangCode("target");
        if (!langCode.isEmpty()) {
            return langCode;
        }

        // target languageMap
        Map<String, String> langMap = new HashMap<>();
        langMap.putAll(this.getCommonLangMap());

        langMap.put("Traditional Chinese", "ZH-HANT");
        langMap.put("Portuguese", "pt-PT");
        langMap.put("Portuguese (Brazil)", "pt-BR");

        if (langMap.containsKey(this.targetLanguage)) {
            return langMap.get(this.targetLanguage);
        }

        return "";
    }


    private String getLangCode(String type) throws IOException, DeepLException, InterruptedException {
        // 1. 官方api读取映射关系
        List<Language> languages;

        if ("target".equals(type)) {
            languages = translator.getTargetLanguages();
        } else {
            languages = translator.getSourceLanguages();
        }

        for (Language language : languages) {
            if (language.getName().equals(this.targetLanguage)) {
                return language.getCode();
            }
        }
        return "";
    }

    private Map<String, String> getCommonLangMap() {
        Map<String, String> commonLangMap = new HashMap<>();
        String[] lines = commonLangPairs.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("//")) {
                continue; // 跳过空行和注释
            }

            String[] parts = line.split(":");
            if (parts.length == 2) {
                String key = parts[0].replaceAll("\"", "").trim(); // 去掉引号
                String value = parts[1].replaceAll("'", "").replaceAll(",", "").trim(); // 去掉单引号和逗号
                commonLangMap.put(key, value);
            }
        }

        return commonLangMap;
    }

}
