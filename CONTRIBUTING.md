
# Contributing to Computer Network Lab Assignments

We are excited to have you contribute to the **Computer Network Lab Assignments** repository! Whether you are fixing a bug, adding a new feature, or improving the documentation, your contributions are valuable. Below are the guidelines for contributing to this project.

## Table of Contents

- [How to Contribute](#how-to-contribute)
  - [Reporting Bugs](#reporting-bugs)
  - [Suggesting Features](#suggesting-features)
  - [Submitting Changes](#submitting-changes)
- [Code Style Guidelines](#code-style-guidelines)
- [Pull Request Guidelines](#pull-request-guidelines)
- [Branching Strategy](#branching-strategy)
- [Community Guidelines](#community-guidelines)

## How to Contribute

### Reporting Bugs

If you find a bug in the code, please let us know by [opening an issue](https://github.com/arya2004/computer-networks/issues). Be sure to include:

- A clear and descriptive title.
- A detailed description of the issue, including steps to reproduce.
- The version or branch you are using.
- Any relevant screenshots or log files, if applicable.

### Suggesting Features

We encourage you to suggest features that could enhance the lab assignments. To suggest a feature, please [open an issue](https://github.com/arya2004/computer-networks/issues) and include:

- A clear and descriptive title.
- A detailed explanation of the feature.
- The motivation or problem it solves.
- Any technical suggestions for implementing it (optional).

### Submitting Changes

1. **Fork the repository** and create a branch from `main`.
2. Make your changes, ensuring that:
   - Code follows the [Code Style Guidelines](#code-style-guidelines).
   - Any new or modified functionality is properly documented.
   - New assignments or scripts are added in the appropriate directory.
   - If applicable, update the `README.md` or other relevant documentation.
3. **Test your changes** to ensure they work as expected.
4. Submit a **pull request** with a clear description of the changes and the problem they solve.

## Code Style Guidelines

We expect all contributors to follow the coding style used in the project to maintain consistency. Here are some guidelines:

### For Python Code:
- Use 4 spaces for indentation (no tabs).
- Follow PEP8 standards for Python code.
- Use meaningful variable and function names.
- Include docstrings for functions and classes.

### For C/C++ Code:
- Use consistent indentation (4 spaces or tabs as per the project style).
- Follow the C99 or C++11/14 standard unless specified otherwise.
- Use comments to explain non-trivial code.

### General:
- Ensure your code is modular and follows good software design principles.
- Avoid hard-coding values when possible; use configuration or constants.
- Include error handling where necessary.

## Pull Request Guidelines

When submitting a pull request:

- Ensure your changes are based on the `main` branch.
- Keep the scope of your pull request focused on a single task or feature. If you're addressing multiple issues, consider creating separate pull requests for each.
- Provide a clear description of the changes, including:
  - The problem being addressed.
  - The solution implemented.
  - Any potential side effects or considerations.
- Include tests, if applicable, to verify the behavior of your code.
- Reference related issues in the pull request description (e.g., `Fixes #123`).

### Example Pull Request Description:

```markdown
### Description
This pull request implements a simple TCP client-server chat application. The server can handle multiple clients simultaneously using threading.

### Changes
- Added `chat_server.py` and `chat_client.py` scripts for the chat application.
- Updated `README.md` to include instructions on how to run the chat application.
- Fixed issue with socket timeout in previous TCP server implementation.

### Issue Reference
Fixes #45

### Testing
Tested locally with multiple clients connecting to the server. Verified that the server correctly handles messages and client disconnections.
```

## Branching Strategy

- **`main` branch**: This branch contains the stable, tested code. Any new features should be merged into `main` after review.
- **Feature branches**: For any new features or bug fixes, create a new branch from `main` with a descriptive name (e.g., `feature-chat-app` or `bugfix-timeout-issue`).
- After your changes are ready and tested, submit a pull request to merge your feature branch into `main`.

## Community Guidelines

We are committed to creating a welcoming and inclusive community. Please adhere to the following guidelines when interacting with others:

- Be respectful and considerate of others.
- Provide constructive feedback when reviewing code or discussing ideas.
- Report any behavior that violates these guidelines to the repository maintainers.

By contributing to this project, you agree to abide by the [Code of Conduct](https://github.com/arya2004/computer-networks/code-of-conduct).

---

Thank you for contributing! We look forward to your input and ideas.
