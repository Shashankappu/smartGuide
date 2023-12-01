#src/main/python/model.py

from sklearn.feature_extraction.text import TfidfVectorizer

def extract_keywords(paragraph, num_keywords=5):
    # Create the TF-IDF vectorizer
    vectorizer = TfidfVectorizer(stop_words='english')

    # Fit and transform the vectorizer on the provided paragraph
    tfidf_matrix = vectorizer.fit_transform([paragraph])

    # Get feature names (words)
    feature_names = vectorizer.get_feature_names_out()

    # Get the indices of the top N keywords based on TF-IDF scores
    top_keyword_indices = tfidf_matrix.sum(axis=0).argsort()[:, ::-1][:, :num_keywords]

    # Extract the top keywords from the feature names
    top_keywords = [feature_names[index] for index in top_keyword_indices.tolist()[0]]

    return top_keywords

