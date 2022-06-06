using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.ProBuilder;

public class WaterController : MonoBehaviour {
    private ProBuilderMesh _mesh;
    private Vertex[] _vertices;

    void Start() {
        _mesh = gameObject.GetComponent<ProBuilderMesh>();
        _vertices = _mesh.GetVertices();
        for (int i = 0; i < _vertices.Length; i++) {
            //Debug.Log( _vertices[i].position);
            _vertices[i].position = new Vector3(_vertices[i].position.x, _vertices[i].position.y, _vertices[i].position.z);
        }
        _mesh.SetVertices(_vertices, true);
    }
    
    void Update() {
       
      
    }
}
