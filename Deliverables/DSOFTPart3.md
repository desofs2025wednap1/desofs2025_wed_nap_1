# **Sprint 2 Delivery Report**

This report summarizes the work completed during the second sprint of Phase 2, focusing on the implementation of automated pipelines and the results from the OWASP Application Security Verification Standard (ASVS) checklist.

Group Members:

* 1211151 - Alexandre Geração
* 1240449 - Eduardo Fernandes
* 1242314 - Filip Frýdl
* 1211905 - Enmiao Ye
 1211613 - Pedro Nogueira

---

### **Implemented Pipelines**

For this phase of the project, the focus was on automating the development, testing, and deployment processes to enhance security and efficiency. The evaluation criteria for this sprint emphasized the automation of several key practices:

* **Continuous Integration/Continuous Deployment (CI/CD):** The project required the implementation of a DevSecOps pipeline. The goal was to automate builds, testing (including static and dynamic analysis), and security scanning to ensure that the developed functionalities were delivered in a secure and automated fashion.
* **Automated Security Scanning:** The pipeline incorporated several types of automated security analysis:
    * **Static Application Security Testing (SAST):** To analyze source code for potential vulnerabilities.
    * **Software Composition Analysis (SCA):** To identify vulnerabilities in third-party components and libraries.
    * **Dynamic Application Security Testing (DAST):** To test the running application for security flaws.
* **Infrastructure and Configuration:** The sprint also included security configuration, installation, and an overall security assessment of the final application.

### Automated Pipelines Analysis

Our project now includes a comprehensive set of automated pipelines designed to ensure code quality, security, and integrity. These pipelines are managed through GitHub Actions and are defined in the `.github/workflows` directory.

#### The Superpipeline

The centerpiece of our automation is the **Superpipeline**, defined in `superpipeline.yml`. This pipeline consolidates multiple quality and security checks into a single, manually triggered workflow. It is composed of the following jobs:

* **Quality-Assurance**: This job leverages the `super-linter` to perform static analysis on the codebase. It checks for code smells, style violations, and other quality issues across multiple programming languages.
* **Unit-and-Integration-Tests**: This job is responsible for building the application and running our full suite of automated tests. It uses **Maven** to execute both unit and integration tests, ensuring that new changes do not break existing functionality.
* **Security-Audit**: A crucial component of our DevSecOps approach, this job runs a battery of security scans to identify potential vulnerabilities:
    * **Gitleaks**: Scans the repository for hardcoded secrets, such as API keys and passwords.
    * **Snyk**: Performs both Static Application Security Testing (SAST) to find vulnerabilities in our source code and Software Composition Analysis (SCA) to identify security issues in our third-party dependencies.
    * **OWASP ZAP**: Executes Dynamic Application Security Testing (DAST) by scanning the running application for common web application vulnerabilities.
* **Build-and-Push-Docker-Image**: This job, currently commented out, is designed to build and push the application as a Docker image to a container registry, paving the way for automated deployments.

By integrating these tools into a single superpipeline, we have created a robust and efficient workflow that automates the most critical aspects of the development lifecycle, from code quality and testing to security and deployment readiness. This ensures that every change is automatically vetted for quality and security, enabling us to deliver a more secure and reliable application.

---

### **ASVS Checklist Results**

![ASVS Checklist Results](ASVSResults.png)

The project underwent a security assessment using the OWASP ASVS framework. The overall result showed that **46.79%** of the applicable criteria were met. Below is a summary of the results by category:

| Security Category | Validity Percentage |
| :--- | :--- |
| **Malicious Code** | 100% |
| **Validation, Sanitization and Encoding** | 88% |
| **Stored Cryptography** | 64.29% |
| **Session Management** | 58.33% |
| **Error Handling and Logging** | 58.33% |
| **Access Control** | 55.56% |
| **Configuration** | 47.37% |
| **Architecture, Design and Threat Modeling**| 43.24% |
| **Communication** | 40% |
| **API and Web Service** | 36.36% |
| **Authentication** | 29.41% |
| **Data Protection** | 15.38% |
| **Files and Resources** | 15.38% |
| **Business Logic** | 0% |

The results indicate strong performance in areas like **Malicious Code prevention** and **Input Validation**. However, areas such as **Business Logic**, **Data Protection**, and **Authentication** have significant room for improvement.

---

### **Pull Request Analysis**

