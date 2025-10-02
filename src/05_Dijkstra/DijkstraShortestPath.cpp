#include<bits/stdc++.h>
using namespace std;


class DijkstraShortestPath{
    public:
    // function to add edges to the graph
    // the adj vector will store a list of pairs of {node,distance} for each node, eg: adj[2] = {{1(a node),2(its distance from 2)},{3(a node),4(its distance from 2)}}
    void addEdge(int u, int v, int w, vector<vector<pair<int, int>>> &adj){
        adj[u].push_back({v,w});
        adj[v].push_back({u,w}); // cause it is an undirected graph and edges are bidirectional
    }
    //returns the shortest distance from source to all other nodes in a vector
    vector<int> dijkstra(int V, vector<vector<pair<int, int>>> &adj, int source){
        // this pq will store {dist,node} and keep the minimum dist node at top
        priority_queue<pair<int, int>, vector<pair<int, int>>, greater<pair<int, int>>> pq;
        // initializing distance vector with infinite distance as none of the nodes are visited yet
        vector<int> dist(V, INT_MAX);

        dist[source] = 0; // distance of source node from itself is 0
        pq.push({0, source}); // pusing the first node

        while(!pq.empty()){
            // greedily pick the minimum distance node from the priority queue
            int node = pq.top().second;
            int dis = pq.top().first;

            pq.pop();

            // bfs for all its adjacent nodes
            for(auto it:adj[node]){
                int adjnode = it.first;
                int adjdist = it.second;

                // now we will cal the distance to reach this adjnode from the path of the node we picked greedily
                // and if its smaller than the already stored distance (in dist vector), then we will update it and push it to the pq
                if(dis + adjdist < dist[adjnode]){
                    dist[adjnode] = dis + adjdist;
                    pq.push({dist[adjnode],adjnode});
                }
            }

        }
        return dist;

    }
    // just to print the shortest distances from source to all other nodes
    void printShortestPaths(int V, vector<int> &dist){
        cout<<"Node \t Distance from Source\n";
        for(int i=0;i<V;i++){
            cout<<"Node: "<<i<<" is at a distance (shortest) "<<dist[i]<<" from the source"<<"\n";
        }
    }


};




int main() {
    // number of vertices
    int V = 5;

    // we will take source node to be 0 (any can be taken)
    int source = 0;

    DijkstraShortestPath graph; // object of the class

    // adding edges, the 5 nodes are 0,1,2,3,4
    vector<vector<pair<int, int>>> adj(V); // adjacency list initialization
    graph.addEdge(0, 1, 9,adj);
    graph.addEdge(0, 2, 6,adj);
    graph.addEdge(0, 3, 5,adj);
    graph.addEdge(0, 4, 3,adj);

    graph.addEdge(2, 1, 2,adj);
    graph.addEdge(2, 3, 4,adj);

    // storing the shortest distances from source to all other nodes
    vector<int> dist = graph.dijkstra(V, adj, source);

    // printing the results
    graph.printShortestPaths(V, dist);

    return 0;
}