#include <bits/stdc++.h>
using namespace std;

struct Point { int x, y; };

enum Encoding {
    UnipolarNRZ = 0,
    BipolarNRZL = 1,
    BipolarNRZI = 2,
    PolarRZ     = 3,
    BipolarAMI  = 4,
    PseudoTernary = 5,
    Manchester  = 6,
    DifferentialManchester = 7,
    MLT3        = 8
};

vector<Point> gen_unipolar_nrz(const string &s, int startX, int unitW, int baseY, int highY){
    vector<Point> pts;
    for(size_t i=0;i<s.size();++i){
        int x0 = startX + i*unitW;
        int x1 = x0 + unitW;
        int y = (s[i]=='1') ? highY : baseY;
        if(pts.empty()) pts.push_back({x0, y});
        else pts.push_back({x0, pts.back().y});
        pts.push_back({x1, y});
    }
    return pts;
}


vector<Point> gen_bipolar_nrzl(const string &s, int startX, int unitW, int centerY, int amplitude){
    vector<Point> pts;
    for(size_t i=0;i<s.size();++i){
        int x0 = startX + i*unitW;
        int x1 = x0 + unitW;
        // '1' -> positive level, '0' -> negative level (no zero level)
        int y = (s[i]=='1') ? centerY - amplitude : centerY + amplitude;
        if(pts.empty()) pts.push_back({x0, y});
        else pts.push_back({x0, pts.back().y});
        pts.push_back({x1, y});
    }
    return pts;
}


vector<Point> gen_bipolar_nrzi(const string &s, int startX, int unitW, int centerY, int amplitude){
    vector<Point> pts;
    // Start with negative level, toggle to positive/negative on each '1'
    bool isPositive = false; // Start at negative level
    
    for(size_t i=0;i<s.size();++i){
        int x0 = startX + i*unitW;
        int x1 = x0 + unitW;
        
        if(s[i]=='1'){
            // Toggle level on '1'
            isPositive = !isPositive;
        }
        // Stay at current level on '0'
        
        int y = isPositive ? centerY - amplitude : centerY + amplitude;
        if(pts.empty()) pts.push_back({x0, y});
        else pts.push_back({x0, pts.back().y});
        pts.push_back({x1, y});
    }
    return pts;
}

vector<Point> gen_polar_rz(const string &s, int startX, int unitW, int baseY, int highY){
    vector<Point> pts;
    for(size_t i=0;i<s.size();++i){
        int x0 = startX + i*unitW;
        int xMid = x0 + unitW/2;
        int x1 = x0 + unitW;
        if(s[i]=='1'){
            // high for first half, return to zero for second half
            if(pts.empty()) pts.push_back({x0, highY});
            else pts.push_back({x0, pts.back().y});
            pts.push_back({xMid, highY});
            pts.push_back({xMid, baseY});
            pts.push_back({x1, baseY});
        } else {
            // zero entire bit
            if(pts.empty()) pts.push_back({x0, baseY});
            else pts.push_back({x0, pts.back().y});
            pts.push_back({x1, baseY});
        }
    }
    return pts;
}


vector<Point> gen_bipolar_ami(const string &s, int startX, int unitW, int centerY, int amplitude){
    vector<Point> pts;
    bool nextPositive = true;
    for(size_t i=0;i<s.size();++i){
        int x0 = startX + i*unitW;
        int x1 = x0 + unitW;
        int y;
        if(s[i]=='1'){
            y = nextPositive ? centerY - amplitude : centerY + amplitude;
            nextPositive = !nextPositive;
        } else {
            y = centerY;
        }
        if(pts.empty()) pts.push_back({x0, y});
        else pts.push_back({x0, pts.back().y});
        pts.push_back({x1, y});
    }
    return pts;
}


vector<Point> gen_pseudoternary(const string &s, int startX, int unitW, int centerY, int amplitude){
    vector<Point> pts;
    bool nextPositive = true;
    for(size_t i=0;i<s.size();++i){
        int x0 = startX + i*unitW;
        int x1 = x0 + unitW;
        int y;
        if(s[i]=='0'){
            y = nextPositive ? centerY - amplitude : centerY + amplitude;
            nextPositive = !nextPositive;
        } else {
            y = centerY;
        }
        if(pts.empty()) pts.push_back({x0, y});
        else pts.push_back({x0, pts.back().y});
        pts.push_back({x1, y});
    }
    return pts;
}


vector<Point> gen_manchester(const string &s, int startX, int unitW, int baseY, int highY){
    vector<Point> pts;
    for(size_t i=0;i<s.size();++i){
        int x0 = startX + i*unitW;
        int xMid = x0 + unitW/2;
        int x1 = x0 + unitW;
        if(s[i]=='0'){
            // 0 -> low then high (first half low, second half high)
            if(pts.empty()) pts.push_back({x0, baseY});
            else pts.push_back({x0, pts.back().y});
            pts.push_back({xMid, baseY});
            pts.push_back({xMid, highY});
            pts.push_back({x1, highY});
        } else {
            // 1 -> high then low
            if(pts.empty()) pts.push_back({x0, highY});
            else pts.push_back({x0, pts.back().y});
            pts.push_back({xMid, highY});
            pts.push_back({xMid, baseY});
            pts.push_back({x1, baseY});
        }
    }
    return pts;
}

vector<Point> gen_differential_manchester(const string &s, int startX, int unitW, int centerY, int amplitude){
    vector<Point> pts;

    int currentLevel = -1;
    
    for(size_t i=0;i<s.size();++i){
        int x0 = startX + i*unitW;
        int xMid = x0 + unitW/2;
        int x1 = x0 + unitW;

        bool transitionAtStart = (s[i]=='1');
        if(transitionAtStart){
            currentLevel = -currentLevel;
        }
        
        int yStart = (currentLevel==1) ? centerY - amplitude : centerY + amplitude;
        

        currentLevel = -currentLevel;
        int yEnd = (currentLevel==1) ? centerY - amplitude : centerY + amplitude;

        if(pts.empty()) pts.push_back({x0, yStart});
        else pts.push_back({x0, pts.back().y});
        pts.push_back({xMid, yStart});
        pts.push_back({xMid, yEnd});
        pts.push_back({x1, yEnd});
    }
    return pts;
}


vector<Point> gen_mlt3(const string &s, int startX, int unitW, int centerY, int step){
    vector<Point> pts;
    // State: 0=zero, 1=positive, 2=zero, 3=negative
    int state = 0;
    
    auto stateY = [&](int st)->int{
        if(st==0) return centerY;
        if(st==1) return centerY - step;
        if(st==2) return centerY;
        return centerY + step;
    };
    
    for(size_t i=0;i<s.size();++i){
        int x0 = startX + i*unitW;
        int x1 = x0 + unitW;
        
        if(s[i]=='1'){
            // Advance state: 0->1, 1->2, 2->3, 3->0
            state = (state + 1) % 4;
        }
        
        int y = stateY(state);
        if(pts.empty()) pts.push_back({x0, y});
        else pts.push_back({x0, pts.back().y});
        pts.push_back({x1, y});
    }
    return pts;
}

string svg_point_list(const vector<Point>& pts){
    stringstream ss;
    for(size_t i=0;i<pts.size();++i){
        ss << pts[i].x << "," << pts[i].y;
        if(i+1<pts.size()) ss << " ";
    }
    return ss.str();
}

void draw_axes_and_labels(stringstream &svg, int width, int height, int originX, int originY, int centerY){
    // X axis
    svg << "<line x1=\"0\" y1=\"" << originY << "\" x2=\"" << width << "\" y2=\"" << originY
        << "\" stroke=\"black\" stroke-width=\"2\" />\n";
    
    // Y axis
    svg << "<line x1=\"" << originX << "\" y1=\"0\" x2=\"" << originX << "\" y2=\"" << height
        << "\" stroke=\"black\" stroke-width=\"2\" />\n";
    
    // Center reference line (for bipolar signals)
    svg << "<line x1=\"0\" y1=\"" << centerY << "\" x2=\"" << width << "\" y2=\"" << centerY
        << "\" stroke=\"gray\" stroke-width=\"1\" stroke-dasharray=\"5,5\" opacity=\"0.5\" />\n";
    
    // X-axis ticks every 50 px
    int x = originX + 50;
    int tick = 0;
    while(x < width - 20){
        svg << "<line x1=\"" << x << "\" y1=\"" << originY-5 << "\" x2=\"" << x << "\" y2=\"" << originY+5
            << "\" stroke=\"black\" stroke-width=\"1\" />\n";
        stringstream lab;
        lab << fixed << setprecision(1) << (float)tick + 1.0f;
        svg << "<text x=\"" << x-6 << "\" y=\"" << originY+20 << "\" font-family=\"monospace\" font-size=\"12\">" 
            << lab.str() << "</text>\n";
        x += 50;
        ++tick;
    }
    
    // Y-axis ticks every 50 px
    int y = originY - 50;
    int idx = 0;
    while(y > 20){
        svg << "<line x1=\"" << originX-5 << "\" y1=\"" << y << "\" x2=\"" << originX+5 << "\" y2=\"" << y
            << "\" stroke=\"black\" stroke-width=\"1\" />\n";
        svg << "<text x=\"" << originX-30 << "\" y=\"" << y+4 << "\" font-family=\"monospace\" font-size=\"12\">" 
            << idx << "</text>\n";
        y -= 50;
        ++idx;
    }
    
    // Axis labels
    svg << "<text x=\"" << width-60 << "\" y=\"" << originY+20 << "\" font-family=\"monospace\" font-size=\"16\" fill=\"red\">Time</text>\n";
    svg << "<text x=\"" << originX-35 << "\" y=\"" << 20 << "\" font-family=\"monospace\" font-size=\"16\" fill=\"blue\">V</text>\n";
}

int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cout << "Enter binary string (e.g. 1011001): ";
    string s;
    if(!getline(cin, s)) return 0;
    
    // Strip spaces
    s.erase(remove_if(s.begin(), s.end(), ::isspace), s.end());
    if(s.empty()){
        cout << "Empty string. Exiting.\n";
        return 0;
    }
    
    cout << "\nEncoding options:\n";
    cout << "0: Unipolar NRZ\n";
    cout << "1: Bipolar NRZ-L\n";
    cout << "2: Bipolar NRZ-I\n";
    cout << "3: Polar RZ\n";
    cout << "4: Bipolar AMI\n";
    cout << "5: Pseudo-Ternary\n";
    cout << "6: Manchester\n";
    cout << "7: Differential Manchester\n";
    cout << "8: MLT-3\n";
    cout << "Enter encoding number [0-8]: ";
    
    int enc;
    if(!(cin >> enc)) return 0;
    if(enc < 0 || enc > 8) {
        cout << "Invalid encoding. Exiting.\n";
        return 0;
    }

    // SVG canvas parameters
    const int width = 1200;
    const int height = 700;
    const int startX = 50;
    const int unitW = 50;
    const int baseY = height - 100;
    const int highY = baseY - 200;
    const int centerY = height/2;
    const int amplitude = 80;
    const int step = 60;

    vector<Point> pts;
    string encodingName;

    switch(enc){
        case UnipolarNRZ:
            pts = gen_unipolar_nrz(s, startX, unitW, baseY, highY);
            encodingName = "Unipolar NRZ";
            break;
        case BipolarNRZL:
            pts = gen_bipolar_nrzl(s, startX, unitW, centerY, amplitude);
            encodingName = "Bipolar NRZ-L";
            break;
        case BipolarNRZI:
            pts = gen_bipolar_nrzi(s, startX, unitW, centerY, amplitude);
            encodingName = "Bipolar NRZ-I";
            break;
        case PolarRZ:
            pts = gen_polar_rz(s, startX, unitW, baseY, highY);
            encodingName = "Polar RZ";
            break;
        case BipolarAMI:
            pts = gen_bipolar_ami(s, startX, unitW, centerY, amplitude);
            encodingName = "Bipolar AMI";
            break;
        case PseudoTernary:
            pts = gen_pseudoternary(s, startX, unitW, centerY, amplitude);
            encodingName = "Pseudo-Ternary";
            break;
        case Manchester:
            pts = gen_manchester(s, startX, unitW, baseY, highY);
            encodingName = "Manchester";
            break;
        case DifferentialManchester:
            pts = gen_differential_manchester(s, startX, unitW, centerY, amplitude);
            encodingName = "Differential Manchester";
            break;
        case MLT3:
            pts = gen_mlt3(s, startX, unitW, centerY, step);
            encodingName = "MLT-3";
            break;
        default:
            cout << "Not implemented\n";
            return 0;
    }

    // Print points to console
    cout << "\nGenerated points (x,y):\n";
    for(size_t i=0;i<pts.size();++i){
        cout << "(" << pts[i].x << "," << pts[i].y << ")";
        if(i+1<pts.size()) cout << ", ";
        if((i+1)%6==0) cout << "\n";
    }
    cout << "\n";

    // Build SVG
    stringstream svg;
    svg << "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
    svg << "<svg xmlns=\"http://www.w3.org/2000/svg\" ";
    svg << "width=\"" << width << "\" height=\"" << height << "\" viewBox=\"0 0 " << width << " " << height << "\">\n";
    svg << "<rect width=\"100%\" height=\"100%\" fill=\"#f8f8f8\" />\n";

    // Axes and labels
    draw_axes_and_labels(svg, width, height, startX, baseY, centerY);

    // Polyline for waveform
    svg << "<polyline points=\"" << svg_point_list(pts) << "\" ";
    svg << "fill=\"none\" stroke=\"#0066cc\" stroke-width=\"2.5\" stroke-linejoin=\"round\" stroke-linecap=\"round\" />\n";

    // Title and signal info
    svg << "<text x=\"" << width/2 << "\" y=\"30\" font-family=\"Arial,sans-serif\" font-size=\"20\" font-weight=\"bold\" text-anchor=\"middle\">" 
        << encodingName << "</text>\n";
    svg << "<text x=\"" << startX << "\" y=\"60\" font-family=\"monospace\" font-size=\"16\">Binary: " << s << "</text>\n";

    svg << "</svg>\n";

    ofstream ofs("output.svg");
    if(!ofs){
        cerr << "Failed to open output.svg for writing\n";
        return 0;
    }
    ofs << svg.str();
    ofs.close();

    cout << "SVG written to output.svg. Open it in a browser to view the waveform.\n";
    return 0;
}