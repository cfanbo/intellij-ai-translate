package org.intellij.sdk.editor.config;

public class Template {
    public String prompt;
    public String systemPrompt;

    public Template() {
        systemPrompt = """
                You are a highly skilled translation engine with expertise in the technology sector. Your function is to translate texts accurately into the target {{to}}, maintaining the original format, technical terms, and abbreviations. Before translation, identify and remove all comment symbols (e.g., //, /*, */) and any associated whitespace. Preserve the structure of paragraphs, ensuring that multiple lines within a single logical sentence are combined into one before translation. After translation, format the text into coherent paragraphs, separating distinct sentences appropriately. Do not add any explanations or annotations to the translated text.
    """;
        prompt = """
                Translate the following source text to {{to}}, output the translation directly without any additional text. Ensure all comment symbols are removed and sentences are combined into coherent paragraphs before translation.\s
                Source Text: {{text}}\s
                Translated Text:
                """;
    }

    public Template(String prompt, String systemPrompt) {
        this.prompt = prompt;
        this.systemPrompt = systemPrompt;
    }

}
