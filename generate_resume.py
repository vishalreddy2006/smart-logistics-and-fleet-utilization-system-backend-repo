#!/usr/bin/env python3
from reportlab.lib.pagesizes import letter
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import inch
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, PageBreak
from reportlab.lib.enums import TA_LEFT, TA_CENTER
from reportlab.pdfgen import canvas
from reportlab.lib import colors
from datetime import datetime
import os

# Output path
output_path = os.path.join(os.path.dirname(__file__), "Kalluri_Vishal_Reddy_Resume.pdf")

# Create PDF
doc = SimpleDocTemplate(output_path, pagesize=letter, topMargin=0.4*inch, bottomMargin=0.4*inch, leftMargin=0.5*inch, rightMargin=0.5*inch)

# Create styles
styles = getSampleStyleSheet()
heading_style = ParagraphStyle(
    'CustomHeading',
    parent=styles['Normal'],
    fontSize=12,
    textColor=colors.black,
    spaceAfter=6,
    leading=12,
    fontName='Helvetica-Bold'
)

section_style = ParagraphStyle(
    'SectionHeading',
    parent=styles['Normal'],
    fontSize=11,
    textColor=colors.black,
    spaceAfter=6,
    leading=11,
    fontName='Helvetica-Bold',
    borderPadding=0,
    borderWidth=0
)

contact_style = ParagraphStyle(
    'ContactInfo',
    parent=styles['Normal'],
    fontSize=9,
    textColor=colors.black,
    spaceAfter=2,
    leading=10,
    fontName='Helvetica',
    alignment=TA_CENTER
)

name_style = ParagraphStyle(
    'NameStyle',
    parent=styles['Normal'],
    fontSize=14,
    textColor=colors.black,
    spaceAfter=2,
    leading=14,
    fontName='Helvetica-Bold',
    alignment=TA_CENTER
)

body_style = ParagraphStyle(
    'BodyStyle',
    parent=styles['Normal'],
    fontSize=9,
    textColor=colors.black,
    spaceAfter=4,
    leading=11,
    fontName='Helvetica',
    leftIndent=0.15*inch,
    bulletIndent=0.1*inch,
    alignment=TA_LEFT
)

summary_style = ParagraphStyle(
    'SummaryStyle',
    parent=styles['Normal'],
    fontSize=9,
    textColor=colors.black,
    spaceAfter=6,
    leading=11,
    fontName='Helvetica',
    alignment=TA_LEFT
)

# Build content
story = []

# Header
story.append(Paragraph("KALLURI VISHAL REDDY", name_style))
story.append(Spacer(1, 0.08*inch))
story.append(Paragraph("Phone: +91-6303967435 | Email: kallurivishal912@gmail.com | GitHub: https://github.com/vishalreddy2006", contact_style))
story.append(Spacer(1, 0.15*inch))

# Professional Summary
story.append(Paragraph("<b>PROFESSIONAL SUMMARY</b>", section_style))
story.append(Paragraph("Computer Science Engineering student with interest in cybersecurity and Security Operations Center (SOC) practices. Hands-on exposure to basic network analysis and reconnaissance using tools like Wireshark and Nmap. Currently learning attack patterns, basic log analysis, and threat detection concepts. Looking for a SOC/Blue Team trainee opportunity to build practical skills in monitoring and security analysis.", summary_style))
story.append(Spacer(1, 0.12*inch))

# Technical Skills
story.append(Paragraph("<b>TECHNICAL SKILLS</b>", section_style))
story.append(Paragraph("• <b>Security Basics:</b> Reconnaissance, basic attack awareness, vulnerability basics", body_style))
story.append(Paragraph("• <b>Networking:</b> TCP/IP, DNS, HTTP/HTTPS, ports and protocols", body_style))
story.append(Paragraph("• <b>Tools:</b> Wireshark (basic), Nmap, Kali Linux", body_style))
story.append(Paragraph("• <b>Operating Systems:</b> Linux (basic CLI), Windows", body_style))
story.append(Spacer(1, 0.12*inch))

# Projects
story.append(Paragraph("<b>PROJECTS</b>", section_style))
story.append(Paragraph("<b>ProShield – Threat Analysis</b><br/>GitHub: https://github.com/vishalreddy2006/Proshield-AI", body_style))
story.append(Paragraph("• Developed a basic system to analyze security events and identify simple attack patterns", body_style))
story.append(Paragraph("• Implemented logic to detect repeated login attempts and identify brute-force attack behavior", body_style))
story.append(Paragraph("• Identified brute-force login patterns using simple log analysis techniques", body_style))
story.append(Paragraph("• Explored mapping of activities to attack stages using MITRE ATT&CK framework (beginner level)", body_style))
story.append(Paragraph("• Focused on understanding how security events can be detected and interpreted in a SOC environment", body_style))
story.append(Spacer(1, 0.12*inch))

# Practical Exposure
story.append(Paragraph("<b>PRACTICAL EXPOSURE</b>", section_style))
story.append(Paragraph("• Observed network traffic using Wireshark (TCP handshake, DNS requests)", body_style))
story.append(Paragraph("• Performed basic port scanning using Nmap and identified open ports and services", body_style))
story.append(Paragraph("• Practiced networking and basic security concepts through TryHackMe labs", body_style))
story.append(Spacer(1, 0.12*inch))

# SOC Learning
story.append(Paragraph("<b>SOC LEARNING (ENTRY LEVEL)</b>", section_style))
story.append(Paragraph("• Understanding of how monitoring and alerting works in a Security Operations Center (SOC)", body_style))
story.append(Paragraph("• Familiar with common attack types such as brute force and scanning", body_style))
story.append(Paragraph("• Basic exposure to how logs and security events are used for detection", body_style))
story.append(Paragraph("• Familiar with fundamentals of log monitoring and security event analysis", body_style))
story.append(Spacer(1, 0.12*inch))

# Education
story.append(Paragraph("<b>EDUCATION</b>", section_style))
story.append(Paragraph("<b>B.Tech in Computer Science Engineering</b><br/>KL University | Expected Graduation: 2028 | CGPA: 8.6", body_style))
story.append(Spacer(1, 0.12*inch))

# Areas of Interest
story.append(Paragraph("<b>AREAS OF INTEREST</b>", section_style))
story.append(Paragraph("Cybersecurity | SOC (Beginner) | Network Security", body_style))

# Build PDF
doc.build(story)
print(f"Resume generated successfully: {output_path}")
