python
from transformers import AutoTokenizer, AutoModelForCausalLM, pipeline
import gradio as gr

# Load model and tokenizer
model_id = "PygmalionAI/pygmalion-6b"
tokenizer = AutoTokenizer.from_pretrained(model_id)
model = AutoModelForCausalLM.from_pretrained(
    model_id,
    device_map="auto",  # Defaults to CPU on free tier
    low_cpu_mem_usage=True
)

generator = pipeline("text-generation", model=model, tokenizer=tokenizer)

# Build prompt dynamically for any character
def build_prompt(character_name, persona, backstory, memory_logs, chat_history, user_input):
    memory_text = "\n".join(f"- {m}" for m in memory_logs if m.strip())
    history_text = "\n".join(chat_history[-4:])  # Limit to last 4 exchanges

    prompt = f"""
{character_name}'s Persona: {persona}
Backstory: {backstory}
Memory:
{memory_text}
<START>
{history_text}
You: {user_input}
{character_name}:
""".strip()

    return prompt

# Chat function
def chat(character_name, persona, backstory, memory_logs, chat_history, user_input):
    prompt = build_prompt(character_name, persona, backstory, memory_logs.split("\n"), chat_history.split("\n"), user_input)
    response = generator(prompt, max_new_tokens=200, temperature=0.8)[0]["generated_text"]

    # Extract only the character's reply
    reply = response.split(f"{character_name}:")[-1].strip()
    return reply

# Gradio interface
demo = gr.Interface(
    fn=chat,
    inputs=[
        gr.Textbox(label="Character Name", value="Lucien"),
        gr.Textbox(label="Persona", lines=2, value="A sarcastic vampire who regrets turning his brother."),
        gr.Textbox(label="Backstory", lines=3, value="Lucien was once a poet in 18th-century France..."),
        gr.Textbox(label="Memory Logs (one per line)", lines=4, value="- He hates mirrors\n- He misses his brother\n- He drinks only animal blood"),
        gr.Textbox(label="Chat History (one line per message)", lines=6, value="Lucien: Immortality is a curse wrapped in velvet.\nYou: What do you remember about the night you changed?"),
        gr.Textbox(label="Your Message", value="Do you still dream?")
    ],
    outputs="text",
    title="LumaAI Character Chat",
    description="Talk to any character with memory, personality, and backstory. Powered by Pygmalion-6B."
)

demo.launch()
