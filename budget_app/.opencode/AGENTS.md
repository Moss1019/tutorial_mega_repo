# OpenCode Agent Configuration

## Overview
This project is configured to use OpenCode with a local Ollama LLM instance for AI-powered software development tasks.

## Configuration

- **URL**: `localhost:49374`
- **Model**: `qwen3-coder-next:latest`

## Agent Roles

### 1. Implementer Agent
**File**: `./.opencode/agent/implementer.md`

The implementer agent is responsible for:
- Writing and modifying code by implementing TODOs
- Implementing features and bug fixes
- Refactoring existing code
- Running tests and build commands
- Integrating with frameworks and libraries

**Key responsibilities**:
- Analyze requirements and translate them into code
- Use appropriate Flutter/Dart patterns and best practices
- Ensure code follows project conventions
- Test implementations before marking tasks complete

### 2. Reviewer Agent
**File**: `./.opencode/agent/reviewer.md`

The reviewer agent is responsible for:
- Code review and quality assurance
- Identifying potential bugs and issues
- Suggesting improvements to code quality
- Ensuring adherence to project standards
- Verifying that changes meet requirements

**Key responsibilities**:
- Review code contributions from implementer or user
- Check for Flutter/Dart best practices
- Verify test coverage and quality
- Suggest optimizations and refactoring opportunities

## Model Information

Both agents use the same model: `qwen3-coder-next:latest` from Ollama at `localhost:49374`.

This model is optimized for code generation and understanding, making it suitable for both implementation and review tasks.

## Prompt Files Location

All agent prompts are stored in: `./.opencode/agent/`

- `implementer.md` - Implementation agent instructions
- `reviewer.md` - Review agent instructions

## How to Select Specific Agents

In OpenCode, you can select specific agents using the following methods:

### Method 1: Using `/agent` Command
Type `/agent` in the chat and select the desired agent from the list:
- `/agent implementer` - Switch to the implementer agent
- `/agent reviewer` - Switch to the reviewer agent

### Method 2: Using Tab Navigation
- OpenCode automatically uses the `default_agent` (configured as `implementer` in `opencode.json`)
- Press `Ctrl+Tab` or use the TUI to switch between agents
- Each agent maintains its own context and conversation history

### Method 3: Direct Prompting
You can also specify which agent you want to use by mentioning it in your prompt:
- "Implementer, please add a new feature..."
- "Reviewer, please review this code..."

### Configuration File

The agent configuration is stored in `opencode.json`:

```jsonc
{
  "$schema": "https://opencode.ai/config.json",
  "default_agent": "implementer",
  "agents": {
    "implementer": {
      "instructions": "./.opencode/agent/implementer.md"
    },
    "reviewer": {
      "instructions": "./.opencode/agent/reviewer.md"
    }
  }
}
```

### Default Agent

The `default_agent` is set to `implementer` in the configuration. This is the agent that will be used when you start a new session or don't specify an agent.
