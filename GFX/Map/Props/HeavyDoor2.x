xof 0303txt 0032
template XSkinMeshHeader {
 <3cf169ce-ff7c-44ab-93c0-f78f62d172e2>
 WORD nMaxSkinWeightsPerVertex;
 WORD nMaxSkinWeightsPerFace;
 WORD nBones;
}

template VertexDuplicationIndices {
 <b8d65549-d7c9-4995-89cf-53a9a8b031e3>
 DWORD nIndices;
 DWORD nOriginalVertices;
 array DWORD indices[nIndices];
}

template SkinWeights {
 <6f0d123b-bad2-4167-a0d0-80224f25fabb>
 STRING transformNodeName;
 DWORD nWeights;
 array DWORD vertexIndices[nWeights];
 array FLOAT weights[nWeights];
 Matrix4x4 matrixOffset;
}

template AnimTicksPerSecond {
 <9e415a43-7ba6-4a73-8743-b73d47e88476>
 DWORD AnimTicksPerSecond;
}

template FVFData {
 <b6e70a0e-8ef9-4e83-94ad-ecc8b0c04897>
 DWORD dwFVF;
 DWORD nDWords;
 array DWORD data[nDWords];
}


AnimTicksPerSecond {
 24;
}

Frame World {
 

 FrameTransformMatrix {
  1.000000,0.000000,0.000000,0.000000,0.000000,1.000000,0.000000,0.000000,0.000000,0.000000,1.000000,0.000000,0.000000,0.000000,0.000000,1.000000;;
 }

 Mesh World {
  192;
  -12.000000;4.000000;2.000000;,
  68.000000;128.000000;2.000000;,
  68.000000;4.000000;2.000000;,
  -12.000000;64.000000;2.000000;,
  68.000000;4.000000;-2.000000;,
  -12.000000;64.000000;-2.000000;,
  -12.000000;4.000000;-2.000000;,
  68.000000;128.000000;-2.000000;,
  -12.000000;4.000000;2.000000;,
  68.000000;4.000000;-2.000000;,
  -12.000000;4.000000;-2.000000;,
  68.000000;4.000000;2.000000;,
  68.000000;128.000000;2.000000;,
  68.000000;4.000000;-2.000000;,
  68.000000;4.000000;2.000000;,
  68.000000;128.000000;-2.000000;,
  -12.000000;64.000000;2.000000;,
  68.000000;128.000000;-2.000000;,
  68.000000;128.000000;2.000000;,
  -12.000000;64.000000;-2.000000;,
  -12.000000;4.000000;-2.000000;,
  -12.000000;64.000000;2.000000;,
  -12.000000;4.000000;2.000000;,
  -12.000000;64.000000;-2.000000;,
  -12.000000;316.000000;-2.000000;,
  68.000000;192.000000;-2.000000;,
  68.000000;316.000000;-2.000000;,
  -12.000000;256.363586;-2.000000;,
  68.000000;316.000000;2.000000;,
  -12.000000;256.363586;2.000000;,
  -12.000000;316.000000;2.000000;,
  68.000000;192.000000;2.000000;,
  -12.000000;316.000000;-2.000000;,
  68.000000;316.000000;2.000000;,
  -12.000000;316.000000;2.000000;,
  68.000000;316.000000;-2.000000;,
  68.000000;192.000000;-2.000000;,
  68.000000;316.000000;2.000000;,
  68.000000;316.000000;-2.000000;,
  68.000000;192.000000;2.000000;,
  -12.000000;256.363586;-2.000000;,
  68.000000;192.000000;2.000000;,
  68.000000;192.000000;-2.000000;,
  -12.000000;256.363586;2.000000;,
  -12.000000;316.000000;2.000000;,
  -12.000000;256.363586;-2.000000;,
  -12.000000;316.000000;-2.000000;,
  -12.000000;256.363586;2.000000;,
  84.000000;200.000000;2.000000;,
  68.000000;120.000000;2.000000;,
  68.000000;200.000000;2.000000;,
  84.000000;120.000000;2.000000;,
  84.000000;120.000000;-2.000000;,
  68.000000;200.000000;-2.000000;,
  68.000000;120.000000;-2.000000;,
  84.000000;200.000000;-2.000000;,
  68.000000;120.000000;2.000000;,
  84.000000;120.000000;-2.000000;,
  68.000000;120.000000;-2.000000;,
  84.000000;120.000000;2.000000;,
  84.000000;200.000000;2.000000;,
  68.000000;200.000000;-2.000000;,
  84.000000;200.000000;-2.000000;,
  68.000000;200.000000;2.000000;,
  84.000000;120.000000;-2.000000;,
  84.000000;200.000000;2.000000;,
  84.000000;200.000000;-2.000000;,
  84.000000;120.000000;2.000000;,
  68.000000;120.000000;2.000000;,
  68.000000;200.000000;-2.000000;,
  68.000000;200.000000;2.000000;,
  68.000000;120.000000;-2.000000;,
  76.000000;256.000000;7.000000;,
  -5.000000;258.000000;7.000000;,
  76.000000;258.000000;7.000000;,
  -5.000000;256.000000;7.000000;,
  76.000000;258.000000;-7.000000;,
  -5.000000;256.000000;-7.000000;,
  76.000000;256.000000;-7.000000;,
  -5.000000;258.000000;-7.000000;,
  76.000000;256.000000;7.000000;,
  76.000000;258.000000;-7.000000;,
  76.000000;256.000000;-7.000000;,
  76.000000;258.000000;7.000000;,
  -5.000000;258.000000;7.000000;,
  76.000000;258.000000;-7.000000;,
  76.000000;258.000000;7.000000;,
  -5.000000;258.000000;-7.000000;,
  -5.000000;256.000000;7.000000;,
  -5.000000;258.000000;-7.000000;,
  -5.000000;258.000000;7.000000;,
  -5.000000;256.000000;-7.000000;,
  76.000000;256.000000;-7.000000;,
  -5.000000;256.000000;7.000000;,
  76.000000;256.000000;7.000000;,
  -5.000000;256.000000;-7.000000;,
  76.000000;62.000000;7.000000;,
  -5.000000;64.000000;7.000000;,
  76.000000;64.000000;7.000000;,
  -5.000000;62.000000;7.000000;,
  76.000000;64.000000;-7.000000;,
  -5.000000;62.000000;-7.000000;,
  76.000000;62.000000;-7.000000;,
  -5.000000;64.000000;-7.000000;,
  76.000000;62.000000;7.000000;,
  76.000000;64.000000;-7.000000;,
  76.000000;62.000000;-7.000000;,
  76.000000;64.000000;7.000000;,
  -5.000000;64.000000;7.000000;,
  76.000000;64.000000;-7.000000;,
  76.000000;64.000000;7.000000;,
  -5.000000;64.000000;-7.000000;,
  -5.000000;62.000000;7.000000;,
  -5.000000;64.000000;-7.000000;,
  -5.000000;64.000000;7.000000;,
  -5.000000;62.000000;-7.000000;,
  76.000000;62.000000;-7.000000;,
  -5.000000;62.000000;7.000000;,
  76.000000;62.000000;7.000000;,
  -5.000000;62.000000;-7.000000;,
  -4.000000;4.000000;6.000000;,
  92.000000;140.000000;6.000000;,
  92.000000;4.000000;6.000000;,
  -4.000000;64.000000;6.000000;,
  92.000000;4.000000;-6.000000;,
  -4.000000;64.000000;-6.000000;,
  -4.000000;4.000000;-6.000000;,
  92.000000;140.000000;-6.000000;,
  -4.000000;4.000000;6.000000;,
  92.000000;4.000000;-6.000000;,
  -4.000000;4.000000;-6.000000;,
  92.000000;4.000000;6.000000;,
  92.000000;140.000000;6.000000;,
  92.000000;4.000000;-6.000000;,
  92.000000;4.000000;6.000000;,
  92.000000;140.000000;-6.000000;,
  -4.000000;64.000000;6.000000;,
  92.000000;140.000000;-6.000000;,
  92.000000;140.000000;6.000000;,
  -4.000000;64.000000;-6.000000;,
  -4.000000;4.000000;-6.000000;,
  -4.000000;64.000000;6.000000;,
  -4.000000;4.000000;6.000000;,
  -4.000000;64.000000;-6.000000;,
  -4.000000;316.000000;-6.000000;,
  92.000000;180.000000;-6.000000;,
  92.000000;316.000000;-6.000000;,
  -4.000000;256.363586;-6.000000;,
  92.000000;316.000000;6.000000;,
  -4.000000;256.363586;6.000000;,
  -4.000000;316.000000;6.000000;,
  92.000000;180.000000;6.000000;,
  -4.000000;316.000000;-6.000000;,
  92.000000;316.000000;6.000000;,
  -4.000000;316.000000;6.000000;,
  92.000000;316.000000;-6.000000;,
  92.000000;180.000000;-6.000000;,
  92.000000;316.000000;6.000000;,
  92.000000;316.000000;-6.000000;,
  92.000000;180.000000;6.000000;,
  -4.000000;256.363586;-6.000000;,
  92.000000;180.000000;6.000000;,
  92.000000;180.000000;-6.000000;,
  -4.000000;256.363586;6.000000;,
  -4.000000;316.000000;6.000000;,
  -4.000000;256.363586;-6.000000;,
  -4.000000;316.000000;-6.000000;,
  -4.000000;256.363586;6.000000;,
  76.000000;316.000000;-8.000000;,
  92.000000;4.000000;-8.000000;,
  92.000000;316.000000;-8.000000;,
  76.000000;4.000000;-8.000000;,
  92.000000;316.000000;8.000000;,
  76.000000;4.000000;8.000000;,
  76.000000;316.000000;8.000000;,
  92.000000;4.000000;8.000000;,
  76.000000;316.000000;-8.000000;,
  92.000000;316.000000;8.000000;,
  76.000000;316.000000;8.000000;,
  92.000000;316.000000;-8.000000;,
  92.000000;4.000000;-8.000000;,
  92.000000;316.000000;8.000000;,
  92.000000;316.000000;-8.000000;,
  92.000000;4.000000;8.000000;,
  76.000000;4.000000;-8.000000;,
  92.000000;4.000000;8.000000;,
  92.000000;4.000000;-8.000000;,
  76.000000;4.000000;8.000000;,
  76.000000;316.000000;8.000000;,
  76.000000;4.000000;-8.000000;,
  76.000000;316.000000;-8.000000;,
  76.000000;4.000000;8.000000;;
  96;
  3;0,2,1;,
  3;0,1,3;,
  3;4,6,5;,
  3;4,5,7;,
  3;8,10,9;,
  3;8,9,11;,
  3;12,14,13;,
  3;12,13,15;,
  3;16,18,17;,
  3;16,17,19;,
  3;20,22,21;,
  3;20,21,23;,
  3;24,26,25;,
  3;24,25,27;,
  3;28,30,29;,
  3;28,29,31;,
  3;32,34,33;,
  3;32,33,35;,
  3;36,38,37;,
  3;36,37,39;,
  3;40,42,41;,
  3;40,41,43;,
  3;44,46,45;,
  3;44,45,47;,
  3;48,50,49;,
  3;48,49,51;,
  3;52,54,53;,
  3;52,53,55;,
  3;56,58,57;,
  3;56,57,59;,
  3;60,62,61;,
  3;60,61,63;,
  3;64,66,65;,
  3;64,65,67;,
  3;68,70,69;,
  3;68,69,71;,
  3;72,74,73;,
  3;72,73,75;,
  3;76,78,77;,
  3;76,77,79;,
  3;80,82,81;,
  3;80,81,83;,
  3;84,86,85;,
  3;84,85,87;,
  3;88,90,89;,
  3;88,89,91;,
  3;92,94,93;,
  3;92,93,95;,
  3;96,98,97;,
  3;96,97,99;,
  3;100,102,101;,
  3;100,101,103;,
  3;104,106,105;,
  3;104,105,107;,
  3;108,110,109;,
  3;108,109,111;,
  3;112,114,113;,
  3;112,113,115;,
  3;116,118,117;,
  3;116,117,119;,
  3;120,122,121;,
  3;120,121,123;,
  3;124,126,125;,
  3;124,125,127;,
  3;128,130,129;,
  3;128,129,131;,
  3;132,134,133;,
  3;132,133,135;,
  3;136,138,137;,
  3;136,137,139;,
  3;140,142,141;,
  3;140,141,143;,
  3;144,146,145;,
  3;144,145,147;,
  3;148,150,149;,
  3;148,149,151;,
  3;152,154,153;,
  3;152,153,155;,
  3;156,158,157;,
  3;156,157,159;,
  3;160,162,161;,
  3;160,161,163;,
  3;164,166,165;,
  3;164,165,167;,
  3;168,170,169;,
  3;168,169,171;,
  3;172,174,173;,
  3;172,173,175;,
  3;176,178,177;,
  3;176,177,179;,
  3;180,182,181;,
  3;180,181,183;,
  3;184,186,185;,
  3;184,185,187;,
  3;188,190,189;,
  3;188,189,191;;

  MeshNormals {
   192;
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   -0.624695;0.780869;0.000000;,
   -0.624695;0.780869;0.000000;,
   -0.624695;0.780869;0.000000;,
   -0.624695;0.780869;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;1.000000;0.000000;,
   0.000000;1.000000;0.000000;,
   0.000000;1.000000;0.000000;,
   0.000000;1.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   -0.626852;-0.779139;0.000000;,
   -0.626852;-0.779139;0.000000;,
   -0.626852;-0.779139;0.000000;,
   -0.626852;-0.779139;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;-0.000000;,
   -1.000000;-0.000000;0.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;1.000000;0.000000;,
   0.000000;1.000000;0.000000;,
   0.000000;1.000000;0.000000;,
   0.000000;1.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;-0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;-0.000000;-1.000000;,
   -0.000000;0.000000;-1.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;-0.000000;0.000000;,
   0.000000;1.000000;0.000000;,
   0.000000;1.000000;0.000000;,
   -0.000000;1.000000;0.000000;,
   0.000000;1.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;-0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;-0.000000;-1.000000;,
   -0.000000;0.000000;-1.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;-0.000000;0.000000;,
   0.000000;1.000000;0.000000;,
   0.000000;1.000000;0.000000;,
   -0.000000;1.000000;0.000000;,
   0.000000;1.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   -0.620703;0.784046;0.000000;,
   -0.620703;0.784046;0.000000;,
   -0.620703;0.784046;0.000000;,
   -0.620703;0.784046;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;1.000000;0.000000;,
   0.000000;1.000000;0.000000;,
   0.000000;1.000000;0.000000;,
   0.000000;1.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   -0.622523;-0.782601;0.000000;,
   -0.622523;-0.782601;0.000000;,
   -0.622523;-0.782601;0.000000;,
   -0.622523;-0.782601;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;-0.000000;,
   -1.000000;-0.000000;0.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;-1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;0.000000;1.000000;,
   0.000000;1.000000;0.000000;,
   0.000000;1.000000;0.000000;,
   0.000000;1.000000;0.000000;,
   0.000000;1.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   1.000000;0.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   0.000000;-1.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;0.000000;,
   -1.000000;0.000000;-0.000000;,
   -1.000000;-0.000000;0.000000;;
   96;
   3;0,2,1;,
   3;0,1,3;,
   3;4,6,5;,
   3;4,5,7;,
   3;8,10,9;,
   3;8,9,11;,
   3;12,14,13;,
   3;12,13,15;,
   3;16,18,17;,
   3;16,17,19;,
   3;20,22,21;,
   3;20,21,23;,
   3;24,26,25;,
   3;24,25,27;,
   3;28,30,29;,
   3;28,29,31;,
   3;32,34,33;,
   3;32,33,35;,
   3;36,38,37;,
   3;36,37,39;,
   3;40,42,41;,
   3;40,41,43;,
   3;44,46,45;,
   3;44,45,47;,
   3;48,50,49;,
   3;48,49,51;,
   3;52,54,53;,
   3;52,53,55;,
   3;56,58,57;,
   3;56,57,59;,
   3;60,62,61;,
   3;60,61,63;,
   3;64,66,65;,
   3;64,65,67;,
   3;68,70,69;,
   3;68,69,71;,
   3;72,74,73;,
   3;72,73,75;,
   3;76,78,77;,
   3;76,77,79;,
   3;80,82,81;,
   3;80,81,83;,
   3;84,86,85;,
   3;84,85,87;,
   3;88,90,89;,
   3;88,89,91;,
   3;92,94,93;,
   3;92,93,95;,
   3;96,98,97;,
   3;96,97,99;,
   3;100,102,101;,
   3;100,101,103;,
   3;104,106,105;,
   3;104,105,107;,
   3;108,110,109;,
   3;108,109,111;,
   3;112,114,113;,
   3;112,113,115;,
   3;116,118,117;,
   3;116,117,119;,
   3;120,122,121;,
   3;120,121,123;,
   3;124,126,125;,
   3;124,125,127;,
   3;128,130,129;,
   3;128,129,131;,
   3;132,134,133;,
   3;132,133,135;,
   3;136,138,137;,
   3;136,137,139;,
   3;140,142,141;,
   3;140,141,143;,
   3;144,146,145;,
   3;144,145,147;,
   3;148,150,149;,
   3;148,149,151;,
   3;152,154,153;,
   3;152,153,155;,
   3;156,158,157;,
   3;156,157,159;,
   3;160,162,161;,
   3;160,161,163;,
   3;164,166,165;,
   3;164,165,167;,
   3;168,170,169;,
   3;168,169,171;,
   3;172,174,173;,
   3;172,173,175;,
   3;176,178,177;,
   3;176,177,179;,
   3;180,182,181;,
   3;180,181,183;,
   3;184,186,185;,
   3;184,185,187;,
   3;188,190,189;,
   3;188,189,191;;
  }

  MeshTextureCoords {
   192;
   0.062500;-0.015600;,
   -0.250000;-0.500000;,
   -0.250000;-0.015600;,
   0.062500;-0.250000;,
   -0.250000;-0.015600;,
   0.062500;-0.250000;,
   0.062500;-0.015600;,
   -0.250000;-0.500000;,
   0.062500;0.007800;,
   -0.250000;-0.007800;,
   0.062500;-0.007800;,
   -0.250000;0.007800;,
   -0.007800;-0.500000;,
   0.007800;-0.015600;,
   -0.007800;-0.015600;,
   0.007800;-0.500000;,
   0.062500;0.007800;,
   -0.250000;-0.007800;,
   -0.250000;0.007800;,
   0.062500;-0.007800;,
   0.007800;-0.015600;,
   -0.007800;-0.250000;,
   -0.007800;-0.015600;,
   0.007800;-0.250000;,
   0.062500;-1.234400;,
   -0.250000;-0.750000;,
   -0.250000;-1.234400;,
   0.062500;-1.001400;,
   -0.250000;-1.234400;,
   0.062500;-1.001400;,
   0.062500;-1.234400;,
   -0.250000;-0.750000;,
   0.062500;-0.007800;,
   -0.250000;0.007800;,
   0.062500;0.007800;,
   -0.250000;-0.007800;,
   0.007800;-0.750000;,
   -0.007800;-1.234400;,
   0.007800;-1.234400;,
   -0.007800;-0.750000;,
   0.062500;-0.007800;,
   -0.250000;0.007800;,
   -0.250000;-0.007800;,
   0.062500;0.007800;,
   -0.007800;-1.234400;,
   0.007800;-1.001400;,
   0.007800;-1.234400;,
   -0.007800;-1.001400;,
   -0.312500;-0.781300;,
   -0.250000;-0.468800;,
   -0.250000;-0.781300;,
   -0.312500;-0.468800;,
   -0.312500;-0.468800;,
   -0.250000;-0.781300;,
   -0.250000;-0.468800;,
   -0.312500;-0.781300;,
   -0.250000;0.007800;,
   -0.312500;-0.007800;,
   -0.250000;-0.007800;,
   -0.312500;0.007800;,
   -0.312500;0.007800;,
   -0.250000;-0.007800;,
   -0.312500;-0.007800;,
   -0.250000;0.007800;,
   0.007800;-0.468800;,
   -0.007800;-0.781300;,
   0.007800;-0.781300;,
   -0.007800;-0.468800;,
   -0.007800;-0.468800;,
   0.007800;-0.781300;,
   -0.007800;-0.781300;,
   0.007800;-0.468800;,
   0.679700;-1.000000;,
   0.996100;-1.007800;,
   0.679700;-1.007800;,
   0.996100;-1.000000;,
   0.679700;-1.007800;,
   0.996100;-1.000000;,
   0.679700;-1.000000;,
   0.996100;-1.007800;,
   0.410200;-1.000000;,
   0.464800;-1.007800;,
   0.464800;-1.000000;,
   0.410200;-1.007800;,
   0.996100;-0.410200;,
   0.679700;-0.464800;,
   0.679700;-0.410200;,
   0.996100;-0.464800;,
   0.410200;-1.000000;,
   0.464800;-1.007800;,
   0.410200;-1.007800;,
   0.464800;-1.000000;,
   0.679700;-0.464800;,
   0.996100;-0.410200;,
   0.679700;-0.410200;,
   0.996100;-0.464800;,
   0.679700;-1.000000;,
   0.996100;-1.007800;,
   0.679700;-1.007800;,
   0.996100;-1.000000;,
   0.679700;-1.007800;,
   0.996100;-1.000000;,
   0.679700;-1.000000;,
   0.996100;-1.007800;,
   0.410200;-1.000000;,
   0.464800;-1.007800;,
   0.464800;-1.000000;,
   0.410200;-1.007800;,
   0.996100;-0.410200;,
   0.679700;-0.464800;,
   0.679700;-0.410200;,
   0.996100;-0.464800;,
   0.410200;-1.000000;,
   0.464800;-1.007800;,
   0.410200;-1.007800;,
   0.464800;-1.000000;,
   0.679700;-0.464800;,
   0.996100;-0.410200;,
   0.679700;-0.410200;,
   0.996100;-0.464800;,
   -0.681200;-0.144500;,
   -0.915500;-0.410200;,
   -0.915500;-0.144500;,
   -0.681200;-0.261700;,
   -0.915500;-0.144500;,
   -0.681200;-0.261700;,
   -0.681200;-0.144500;,
   -0.915500;-0.410200;,
   -0.681200;-0.125000;,
   -0.915500;-0.148400;,
   -0.681200;-0.148400;,
   -0.915500;-0.125000;,
   -0.715300;-0.410200;,
   -0.686000;-0.144500;,
   -0.715300;-0.144500;,
   -0.686000;-0.410200;,
   -0.710400;-0.138700;,
   -0.944800;-0.162100;,
   -0.944800;-0.138700;,
   -0.710400;-0.162100;,
   -0.686000;-0.144500;,
   -0.715300;-0.261700;,
   -0.715300;-0.144500;,
   -0.686000;-0.261700;,
   -0.681200;-0.753900;,
   -0.915500;-0.488300;,
   -0.915500;-0.753900;,
   -0.681200;-0.637400;,
   -0.915500;-0.753900;,
   -0.681200;-0.637400;,
   -0.681200;-0.753900;,
   -0.915500;-0.488300;,
   -0.681200;-0.148400;,
   -0.915500;-0.125000;,
   -0.681200;-0.125000;,
   -0.915500;-0.148400;,
   -0.686000;-0.488300;,
   -0.715300;-0.753900;,
   -0.686000;-0.753900;,
   -0.715300;-0.488300;,
   -0.690900;-0.007800;,
   -0.925300;0.015600;,
   -0.925300;-0.007800;,
   -0.690900;0.015600;,
   -0.715300;-0.753900;,
   -0.686000;-0.637400;,
   -0.686000;-0.753900;,
   -0.715300;-0.637400;,
   -1.020500;-0.814500;,
   -1.059600;-0.205100;,
   -1.059600;-0.814500;,
   -1.020500;-0.205100;,
   -1.059600;-0.814500;,
   -1.020500;-0.205100;,
   -1.020500;-0.814500;,
   -1.059600;-0.205100;,
   -1.020500;-0.150400;,
   -1.059600;-0.119100;,
   -1.020500;-0.119100;,
   -1.059600;-0.150400;,
   -0.825200;-0.205100;,
   -0.864300;-0.814500;,
   -0.825200;-0.814500;,
   -0.864300;-0.205100;,
   -1.020500;-0.150400;,
   -1.059600;-0.119100;,
   -1.059600;-0.150400;,
   -1.020500;-0.119100;,
   -0.053700;-0.617200;,
   -0.014600;-0.007800;,
   -0.014600;-0.617200;,
   -0.053700;-0.007800;;
  }

  VertexDuplicationIndices {
   192;
   192;
   0,
   1,
   2,
   3,
   4,
   5,
   6,
   7,
   8,
   9,
   10,
   11,
   12,
   13,
   14,
   15,
   16,
   17,
   18,
   19,
   20,
   21,
   22,
   23,
   24,
   25,
   26,
   27,
   28,
   29,
   30,
   31,
   32,
   33,
   34,
   35,
   36,
   37,
   38,
   39,
   40,
   41,
   42,
   43,
   44,
   45,
   46,
   47,
   48,
   49,
   50,
   51,
   52,
   53,
   54,
   55,
   56,
   57,
   58,
   59,
   60,
   61,
   62,
   63,
   64,
   65,
   66,
   67,
   68,
   69,
   70,
   71,
   72,
   73,
   74,
   75,
   76,
   77,
   78,
   79,
   80,
   81,
   82,
   83,
   84,
   85,
   86,
   87,
   88,
   89,
   90,
   91,
   92,
   93,
   94,
   95,
   96,
   97,
   98,
   99,
   100,
   101,
   102,
   103,
   104,
   105,
   106,
   107,
   108,
   109,
   110,
   111,
   112,
   113,
   114,
   115,
   116,
   117,
   118,
   119,
   120,
   121,
   122,
   123,
   124,
   125,
   126,
   127,
   128,
   129,
   130,
   131,
   132,
   133,
   134,
   135,
   136,
   137,
   138,
   139,
   140,
   141,
   142,
   143,
   144,
   145,
   146,
   147,
   148,
   149,
   150,
   151,
   152,
   153,
   154,
   155,
   156,
   157,
   158,
   159,
   160,
   161,
   162,
   163,
   164,
   165,
   166,
   167,
   168,
   169,
   170,
   171,
   172,
   173,
   174,
   175,
   176,
   177,
   178,
   179,
   180,
   181,
   182,
   183,
   184,
   185,
   186,
   187,
   188,
   189,
   190,
   191;
  }

  MeshMaterialList {
   2;
   96;
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   0,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1,
   1;

   Material dirtymetal {
    0.584314;0.584314;0.584314;1.000000;;
    11.313700;
    1.000000;1.000000;1.000000;;
    0.000000;0.000000;0.000000;;

    TextureFilename {
     "GFX/map/textures/dirtymetal.jpg";
    }
   }

   Material containment_doors {
    0.584314;0.584314;0.584314;1.000000;;
    11.313700;
    1.000000;1.000000;1.000000;;
    0.000000;0.000000;0.000000;;

    TextureFilename {
     "GFX/map/textures/containment_doors.jpg";
    }
   }
  }

  XSkinMeshHeader {
   1;
   1;
   1;
  }

  SkinWeights {
   "World";
   192;
   0,
   1,
   2,
   3,
   4,
   5,
   6,
   7,
   8,
   9,
   10,
   11,
   12,
   13,
   14,
   15,
   16,
   17,
   18,
   19,
   20,
   21,
   22,
   23,
   24,
   25,
   26,
   27,
   28,
   29,
   30,
   31,
   32,
   33,
   34,
   35,
   36,
   37,
   38,
   39,
   40,
   41,
   42,
   43,
   44,
   45,
   46,
   47,
   48,
   49,
   50,
   51,
   52,
   53,
   54,
   55,
   56,
   57,
   58,
   59,
   60,
   61,
   62,
   63,
   64,
   65,
   66,
   67,
   68,
   69,
   70,
   71,
   72,
   73,
   74,
   75,
   76,
   77,
   78,
   79,
   80,
   81,
   82,
   83,
   84,
   85,
   86,
   87,
   88,
   89,
   90,
   91,
   92,
   93,
   94,
   95,
   96,
   97,
   98,
   99,
   100,
   101,
   102,
   103,
   104,
   105,
   106,
   107,
   108,
   109,
   110,
   111,
   112,
   113,
   114,
   115,
   116,
   117,
   118,
   119,
   120,
   121,
   122,
   123,
   124,
   125,
   126,
   127,
   128,
   129,
   130,
   131,
   132,
   133,
   134,
   135,
   136,
   137,
   138,
   139,
   140,
   141,
   142,
   143,
   144,
   145,
   146,
   147,
   148,
   149,
   150,
   151,
   152,
   153,
   154,
   155,
   156,
   157,
   158,
   159,
   160,
   161,
   162,
   163,
   164,
   165,
   166,
   167,
   168,
   169,
   170,
   171,
   172,
   173,
   174,
   175,
   176,
   177,
   178,
   179,
   180,
   181,
   182,
   183,
   184,
   185,
   186,
   187,
   188,
   189,
   190,
   191;
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000,
   1.000000;
   1.000000,0.000000,0.000000,0.000000,0.000000,1.000000,0.000000,0.000000,0.000000,0.000000,1.000000,0.000000,0.000000,0.000000,0.000000,1.000000;;
  }
 }
}