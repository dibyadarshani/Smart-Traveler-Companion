import sys
import pickle
from flask import Flask, request, jsonify, Response
import os


import pandas as pd
import numpy as np
from collections import Counter
from sklearn.cluster import KMeans
from sklearn.feature_extraction.text import CountVectorizer, TfidfVectorizer
from sklearn.feature_extraction import text
from sklearn.metrics import jaccard_score


# general filtering
def filter_user(user_df):
    null_age = user_df.ageRange.isnull()
    null_gender = user_df.gender.isnull()
    null_style = user_df.travelStyle.isnull()
    one_thou_pt = (user_df.totalPoints > 200)

    user_filtered = user_df[one_thou_pt][~null_age][~null_gender][~null_style]

    user_filtered = user_filtered[['username', 'ageRange', 'gender', 'travelStyle']]
    return user_filtered


def filter_review(review_df):

    attraction_only = review_df.type == 'Attractions'
    filtered_review_df = review_df[['id', 'username', 'type', 'title', 'text', 'rating', 'taObjectCity']]
    filtered_review_df = filtered_review_df[attraction_only]

    return filtered_review_df

def merge_review_and_user(user_df, review_df):

    merged_df = pd.merge(review_df, user_df, on=['username'])

    return merged_df

def foreign_review_filter(merged_df):

    span_mask1 = (merged_df.username == 'AnaS1')
    span_mask2 = (merged_df.username == 'DaniLK')
    span_mask3 = (merged_df.username == 'Aprile_24')
    non_city_mask = (merged_df.taObjectCity == 'California')
    non_relevant_mask = (merged_df.taObjectCity == 'Yellowstone National Park')

    merged_df = merged_df[~span_mask1][~span_mask2][~span_mask3][~non_city_mask][~non_relevant_mask]

    return merged_df



def popular_city_list(merged_df):

    popular_city = []
    for item, value in Counter(merged_df.taObjectCity).items():
        if value >= 12:
            popular_city.append(item)
    return popular_city



def filter_final(merged_df, popular_city_list):

    final_df = merged_df[merged_df.taObjectCity.isin(popular_city_list)]

    return final_df



# for user feature matrix


def user_feature_filter(final_df):

    feature_temp = final_df[['username', 'ageRange', 'gender', 'travelStyle']]
    feature_temp = feature_temp.drop_duplicates()

    return feature_temp

def travel_style(feature_temp):

    style_lst = [item.split(', ') for item in feature_temp.travelStyle]
    style_serie = pd.Series(style_lst)

    feature_temp['new_travel'] = style_serie.values

    return feature_temp

def travel_matrix(feature_temp):


    style_matrix = feature_temp['new_travel'].apply(pd.Series)
    style_df = pd.get_dummies(style_matrix.apply(pd.Series). \
                  stack()).sum(level=0). \
                  rename(columns = lambda x : x)

    return style_df

def age_gender_dummie(feature_temp):

    feature_temp = pd.get_dummies(feature_temp, \
                                  columns = ['ageRange', 'gender'])

    return feature_temp



def combine_all_dummies(user_df, style_df, personality_df):

    feature_temp = user_df.join(style_df)

    feature_final = feature_temp.drop(['travelStyle', \
                                       'new_travel', \
                                       'gender_male', \
                                       '60+ Traveler', \
                                       'username'], axis =1)

    feature_final.reset_index(drop=True, inplace=True)
    feature_final1 = feature_final.join(personality_df)

    return feature_final1




# user big 5 personality scores

def user_personality_score_merge(personality_df, user_temp):


    with_personality_df = pd.merge(personality_df, user_temp, on = 'username')
    only_per_df = with_personality_df.drop(['username', 'user_id'], axis=1 )

    return only_per_df


def mapping_personality(df):

    new_df = df.copy()
    for i in range(len((df.columns))):
        percentile = np.percentile(df.iloc[:, i], 50)

        new_items = np.array([True if item >= percentile else False for item in df.iloc[:, i]])
        new_df[str(i)]= new_items

    return new_df


def cleaning_personality_df(df):


    new_df = df.drop(['open', 'cons', 'extra', 'agree','neuro'], axis=1)
    new_df.columns = ['open', 'cons', 'extra', 'agree','neuro']

    return new_df


# prep for clustering


def cluster_prep_filter(final_df):

    cluster_input_df = final_df.copy()
    cluster_input_df = cluster_input_df[['title', 'text', 'taObjectCity']]

    return cluster_input_df


def grouping_city_title(cluster_input_df):
    df_title_comb = cluster_input_df.groupby(['taObjectCity']). \
                            apply(lambda x: ' '. \
                            join(x.title)). \
                            reset_index()

    return df_title_comb



def grouping_city_text(cluster_input_df):
    df_text_comb = cluster_input_df.groupby(['taObjectCity']). \
                                    apply(lambda x: ' '. \
                                    join(x.text)). \
                                    reset_index()
    return df_text_comb


def merging_content(left, right):

    cluster_input_df = left.merge(right, on= 'taObjectCity')
    cluster_input_df.columns = ['taObjectCity','title','text']
    cluster_input_df.set_index(['taObjectCity'], drop=True, inplace=True)

    return cluster_input_df


# selected cities from cluster



def selected_cities_in_cluster(cluster_df, city_cluster_idx):


    cluster2_mask = (cluster_df['cluster_k'] == city_cluster_idx)
    up_cluster_df = cluster_df[cluster2_mask]
    up_cluster_df.columns = ['cluster_k', 'taObjectCity']

    return up_cluster_df

def selected_city_df(up_cluster_df, city_df):

    selected_df = pd.merge(up_cluster_df, city_df, how = 'left', on = 'taObjectCity')

    return selected_df


class TravelModelMain():

    def __init__(self, user_df, item_df):

        self.user_df = user_df
        self.item_df = item_df
        # predict --------
        #input
        #self.k_recommendation = k_recommendation

    def cluster_texts(self, corpus):
        """
        Transform texts to Tf-Idf coordinates and cluster texts using K-Means
        """
        #my_additional_stop_words = ['acute', 'good', 'great', 'really', 'just', 'nice', 'like', 'day']
        # my_additional_stop_words = ['acute', 'good', 'great', 'really', 'just', 'nice',
        #                             'like', 'day', 'beautiful', 'visit', 'time', 'don',
        #                             'did', 'place', 'didn', 'did', 'tour', 'sydney','pm',
        #                             'lot', '00', 'inside', 'istanbul', 'doesn','going',
        #                             'right', '15']
        my_additional_stop_words = ['acute', 'good', 'great', 'really',
                                    'just', 'nice', 'like', 'day', 'ok',
                                    'visit', 'did', 'don', 'place', 'london',
                                    'paris','san', 'sydney', 'dubai','diego',
                                    'didn', 'fun', 'venice','boston', 'chicago',
                                    'tour', 'went', 'time', 'vegas', 'museum',
                                    'disney', 'barcelona', 'st', 'pm', 'sf',
                                    'worth', 'beautiful', 'la', 'interesting',
                                    'inside', 'outside', 'experience', 'singapore',
                                    'lot', 'free', 'istanbul', 'food', 'people',
                                    'way']
        stop_words = text.ENGLISH_STOP_WORDS.union(my_additional_stop_words)


        vectorizer = TfidfVectorizer(stop_words= stop_words,
                                     max_features = 500,
                                     lowercase=True)

        tfidf_model = vectorizer.fit_transform(corpus)
        vectors = tfidf_model.toarray()
        cols = vectorizer.get_feature_names()

        return (vectors, cols)


    def cluster(self, vectors, cols, reviews):
        """
        Cluster vecotirzed reviews and create k books a data frame relating the
        k label to the book id
        """
        kmeans = KMeans(4, random_state=10000000).fit(vectors)
        k_books = pd.DataFrame(list(zip(list(kmeans.labels_),
                                    list(reviews.index))),
                                    columns=['cluster_k', 'city_index'])

        ''' added code to print centriod vocab - Print the top n words from all centroids vocab
        '''
        n = 15
        centroids = kmeans.cluster_centers_
        for ind, c in enumerate(centroids):
            #print(ind)
            indices = c.argsort()[-1:-n-1:-1]
            #print([cols[i] for i in indices])
            #print("=="*20)

        #print(k_books.head(190))
        return k_books


    def fit(self, utility_matrix, invert_feature, city_temp, content, reviews):


        self.utility_matrix = utility_matrix
        self.invert_feature = invert_feature
        #print('=========invert_feature=========', invert_feature.tail(10))
        print()
        self.city_temp = city_temp
        #print('=====city_temp=======' ,city_temp.head(80))

        vector, cols = self.cluster_texts(content)
        self.cluster_df = self.cluster(vector, cols, reviews)


################################################################################################



    def predict(self, cluster_id, user_id):


        up_cluster_df = selected_cities_in_cluster(self.cluster_df, cluster_id)
        selected_df = selected_city_df(up_cluster_df, self.city_temp)

        final_rating_lst = []

        for city in selected_df.city_id:
            user_i = user_id
            item = city

            user_arr = self.user_df.features[user_i]
            city_arr = self.item_df[self.item_df.id == item].features.to_numpy()[0]

            als_score = np.dot(user_arr, city_arr)

            final_sim_score = self.jaccard_sim_score(user_i, item, self.invert_feature, self.utility_matrix)
            final_rating = self.overall_rating(als_score, final_sim_score)
            final_pair = (final_rating, item)
            final_rating_lst.append(final_pair)


        rec_cities = self.top_list(final_rating_lst, selected_df)

        return rec_cities



    def jaccard_sim_score(self, udi, cid, user_matrix, util_matrix):
        '''
        takes in user(index) and item
        returns jaccard similarity score
        '''
        overall_rating = 0
        overall_sim = 0
        final_score = 0

        filtered_user = util_matrix[util_matrix.city_id == cid]
        #print(user_matrix.head(10))
        #print(filtered_user)

        for user in filtered_user.user_id.values:

            #print('***type******', user_matrix[user])
            # import pdb; pdb.set_trace()
            sim_score = jaccard_score(list(user_matrix[udi].values), list(user_matrix[user].values))
            rating = filtered_user[(filtered_user.user_id == user)].rating.values[0]
            overall_rating += sim_score * rating
            overall_sim +=sim_score

        print('***type******', list(user_matrix[udi].values))
        final_score = overall_rating / overall_sim

        return final_score


    def overall_rating(self, als_score ,jacc_sim_score):
        alpha = 0.3
        beta = 0.7
        if als_score == 0:
            final_score = jacc_sim_score
        else:
            final_score = alpha * jacc_sim_score + beta * als_score

        return final_score



    def top_list(self, final_rating_lst, selected_df):

        top_lst = sorted(final_rating_lst, reverse = True)[:3]


        rec_items = []
        for rating, city in top_lst:
            row = selected_df.loc[selected_df['city_id'] == city]
            rec_city = row.taObjectCity.values
            rec_items.append(rec_city[0])

        return rec_items



model = pickle.load(open('D:/personalized destination recommendation feature/src/samp.p', 'rb'))


app = Flask(__name__)

@app.route('/inference', methods = ['GET'])
def inference():
    city_id = int(request.args.get('city'))
    user_id = int(request.args.get('user'))
    prediction = model.predict(city_id, user_id)
    print(prediction)
    # call model and do predictions and send back result
    
    return jsonify({'pred1' : prediction[0], 'pred2' : prediction[1], 'pred3' : prediction[2]})


if __name__ == '__main__':
    app.run(host = '0.0.0.0', port = 104, debug = True)
