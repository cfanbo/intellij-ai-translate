package org.intellij.sdk.editor.config;

public class Template {
    public String prompt;
    public String systemPrompt;

    public Template() {
        prompt = "Translate the following source text to {{to}}, Output translation directly without any additional text. \nSource Text: {{text}} \nTranslated Text:";
        systemPrompt = "You are a highly skilled translation engine with expertise in the technology sector. Your function is to translate texts accurately into the target {{to}}, maintaining the original format, technical terms, and abbreviations. Do not add any explanations or annotations to the translated text.";
    }

    public Template(String prompt, String systemPrompt) {
        this.prompt = prompt;
        this.systemPrompt = systemPrompt;
    }

}
