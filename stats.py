#!/usr/bin/env python3
"""
Analyseur de Projet Java
Script pour analyser la structure et les statistiques d'un projet Java
"""

import os
import re
from pathlib import Path
from collections import defaultdict, Counter
from datetime import datetime
import argparse

class JavaProjectAnalyzer:
    def __init__(self, root_path="."):
        self.root_path = Path(root_path)
        self.stats = {
            'files': defaultdict(int),
            'lines': defaultdict(int),
            'words': defaultdict(int),
            'characters': defaultdict(int),
            'methods': 0,
            'classes': 0,
            'interfaces': 0,
            'packages': set(),
            'imports': Counter(),
            'keywords': Counter(),
            'largest_files': [],
            'directory_structure': defaultdict(int)
        }
        
        # Extensions de fichiers à analyser
        self.extensions = {
            '.java': 'Java',
            '.xml': 'XML',
            '.properties': 'Properties',
            '.gradle': 'Gradle',
            '.yml': 'YAML',
            '.yaml': 'YAML',
            '.json': 'JSON',
            '.md': 'Markdown',
            '.txt': 'Text'
        }
        
        # Mots-clés Java à compter
        self.java_keywords = {
            'public', 'private', 'protected', 'static', 'final', 'abstract',
            'class', 'interface', 'extends', 'implements', 'import', 'package',
            'if', 'else', 'for', 'while', 'do', 'switch', 'case', 'return',
            'try', 'catch', 'finally', 'throw', 'throws', 'new', 'this', 'super'
        }

    def analyze_java_file(self, file_path):
        """Analyse spécifique pour les fichiers Java"""
        try:
            with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                content = f.read()
                
            # Compter les classes
            class_pattern = r'\bclass\s+\w+'
            self.stats['classes'] += len(re.findall(class_pattern, content))
            
            # Compter les interfaces
            interface_pattern = r'\binterface\s+\w+'
            self.stats['interfaces'] += len(re.findall(interface_pattern, content))
            
            # Compter les méthodes
            method_pattern = r'\b(public|private|protected).*?\w+\s*\([^)]*\)\s*\{'
            self.stats['methods'] += len(re.findall(method_pattern, content, re.MULTILINE))
            
            # Extraire les packages
            package_pattern = r'package\s+([\w.]+);'
            packages = re.findall(package_pattern, content)
            self.stats['packages'].update(packages)
            
            # Extraire les imports
            import_pattern = r'import\s+([\w.*]+);'
            imports = re.findall(import_pattern, content)
            for imp in imports:
                # Garder seulement le package principal
                main_package = imp.split('.')[0] if '.' in imp else imp
                self.stats['imports'][main_package] += 1
                
            # Compter les mots-clés Java
            words = re.findall(r'\b\w+\b', content.lower())
            for word in words:
                if word in self.java_keywords:
                    self.stats['keywords'][word] += 1
                    
        except Exception as e:
            print(f"Erreur lors de l'analyse de {file_path}: {e}")

    def analyze_file(self, file_path):
        """Analyse générale d'un fichier"""
        try:
            with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                content = f.read()
                lines = content.split('\n')
                
            file_ext = file_path.suffix.lower()
            file_type = self.extensions.get(file_ext, 'Other')
            
            # Statistiques de base
            self.stats['files'][file_type] += 1
            self.stats['lines'][file_type] += len(lines)
            self.stats['words'][file_type] += len(content.split())
            self.stats['characters'][file_type] += len(content)
            
            # Garder trace des plus gros fichiers
            file_size = len(lines)
            self.stats['largest_files'].append((file_path, file_size, file_type))
            
            # Analyse spéciale pour Java
            if file_ext == '.java':
                self.analyze_java_file(file_path)
                
        except Exception as e:
            print(f"Erreur lors de la lecture de {file_path}: {e}")

    def scan_directory(self):
        """Parcourt récursivement le répertoire"""
        print(f"🔍 Analyse du projet en cours...")
        
        for root, dirs, files in os.walk(self.root_path):
            # Ignorer certains dossiers
            dirs[:] = [d for d in dirs if not d.startswith('.') and 
                      d not in ['target', 'build', 'node_modules', '__pycache__']]
            
            root_path = Path(root)
            relative_path = root_path.relative_to(self.root_path)
            self.stats['directory_structure'][str(relative_path)] += len(files)
            
            for file in files:
                file_path = root_path / file
                if file_path.suffix.lower() in self.extensions:
                    self.analyze_file(file_path)

    def display_banner(self):
        """Affiche la bannière du projet"""
        banner = """
╔══════════════════════════════════════════════════════════════╗
║                    📊 ANALYSEUR PROJET JAVA                  ║
║                                                              ║
║          Analyse complète de votre codebase Java            ║
╚══════════════════════════════════════════════════════════════╝
        """
        print(banner)
        print(f"📁 Répertoire analysé: {self.root_path.absolute()}")
        print(f"🕐 Date d'analyse: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        print("=" * 65)

    def display_file_statistics(self):
        """Affiche les statistiques des fichiers"""
        print("\n📁 STATISTIQUES DES FICHIERS")
        print("-" * 40)
        
        total_files = sum(self.stats['files'].values())
        total_lines = sum(self.stats['lines'].values())
        total_words = sum(self.stats['words'].values())
        
        print(f"📊 Total: {total_files} fichiers | {total_lines:,} lignes | {total_words:,} mots")
        print()
        
        # Tableau des types de fichiers
        if self.stats['files']:
            print("Type de fichier    Fichiers    Lignes      Mots")
            print("-" * 50)
            for file_type in sorted(self.stats['files'].keys()):
                files = self.stats['files'][file_type]
                lines = self.stats['lines'][file_type]
                words = self.stats['words'][file_type]
                print(f"{file_type:<15} {files:>8} {lines:>10,} {words:>10,}")

    def display_java_statistics(self):
        """Affiche les statistiques spécifiques à Java"""
        print("\n☕ STATISTIQUES JAVA")
        print("-" * 40)
        
        print(f"🏛️  Classes: {self.stats['classes']}")
        print(f"🔌 Interfaces: {self.stats['interfaces']}")
        print(f"⚙️  Méthodes: {self.stats['methods']}")
        print(f"📦 Packages uniques: {len(self.stats['packages'])}")
        
        # Top imports
        if self.stats['imports']:
            print("\n📚 TOP 10 IMPORTS:")
            for package, count in self.stats['imports'].most_common(10):
                print(f"   {package}: {count}")
        
        # Top mots-clés
        if self.stats['keywords']:
            print("\n🔤 TOP 10 MOTS-CLÉS JAVA:")
            for keyword, count in self.stats['keywords'].most_common(10):
                print(f"   {keyword}: {count}")

    def display_largest_files(self):
        """Affiche les plus gros fichiers"""
        print("\n📈 TOP 10 PLUS GROS FICHIERS")
        print("-" * 50)
        
        largest = sorted(self.stats['largest_files'], key=lambda x: x[1], reverse=True)[:10]
        
        print("Fichier                                    Lignes    Type")
        print("-" * 55)
        for file_path, lines, file_type in largest:
            # Raccourcir le nom si trop long
            name = str(file_path.relative_to(self.root_path))
            if len(name) > 35:
                name = "..." + name[-32:]
            print(f"{name:<38} {lines:>6} {file_type:>8}")

    def display_directory_structure(self):
        """Affiche un aperçu de la structure des dossiers"""
        print("\n🗂️  STRUCTURE DU PROJET")
        print("-" * 40)
        
        # Afficher seulement les dossiers avec des fichiers analysés
        dirs_with_files = [(path, count) for path, count in self.stats['directory_structure'].items() if count > 0]
        dirs_with_files = sorted(dirs_with_files, key=lambda x: x[1], reverse=True)[:15]
        
        print("Dossier                              Fichiers")
        print("-" * 45)
        for directory, file_count in dirs_with_files:
            if len(directory) > 30:
                directory = "..." + directory[-27:]
            print(f"{directory:<35} {file_count:>6}")

    def display_summary(self):
        """Affiche un résumé coloré"""
        total_files = sum(self.stats['files'].values())
        total_lines = sum(self.stats['lines'].values())
        
        summary = f"""
╔══════════════════════════════════════════════════════════════╗
║                           RÉSUMÉ                             ║
╠══════════════════════════════════════════════════════════════╣
║  📊 Total fichiers analysés: {total_files:>30}           ║
║  📏 Total lignes de code: {total_lines:>33,}           ║
║  ☕ Classes Java: {self.stats['classes']:>41}           ║
║  🔌 Interfaces Java: {self.stats['interfaces']:>37}           ║
║  ⚙️  Méthodes Java: {self.stats['methods']:>39}           ║
║  📦 Packages: {len(self.stats['packages']):>45}           ║
╚══════════════════════════════════════════════════════════════╝
        """
        print(summary)

    def run_analysis(self):
        """Lance l'analyse complète"""
        self.display_banner()
        
        if not self.root_path.exists():
            print(f"❌ Erreur: Le répertoire {self.root_path} n'existe pas!")
            return
        
        # Scan du projet
        self.scan_directory()
        
        # Affichage des résultats
        self.display_file_statistics()
        
        # Statistiques Java seulement si des fichiers Java trouvés
        if self.stats['files'].get('Java', 0) > 0:
            self.display_java_statistics()
        
        self.display_largest_files()
        self.display_directory_structure()
        self.display_summary()
        
        print("\n✨ Analyse terminée!")

def main():
    parser = argparse.ArgumentParser(description="Analyse d'un projet Java")
    parser.add_argument("path", nargs="?", default=".", 
                       help="Chemin vers le projet à analyser (défaut: répertoire courant)")
    
    args = parser.parse_args()
    
    analyzer = JavaProjectAnalyzer(args.path)
    analyzer.run_analysis()

if __name__ == "__main__":
    main()