import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { GetResponse, PostResponse } from './model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{

  form!:FormGroup

  @ViewChild('secondFile')
  imageFile! : ElementRef

  selectedFileOne!: File

  payload!: PostResponse
  imageDataOne!: any
  imageDataTwo!: any

  serverUrl = 'http://localhost:8080'

  constructor(private fb:FormBuilder, private httpClient:HttpClient){}

  ngOnInit(): void {
    this.form = this.createForm()
  }

  createForm(): FormGroup {
    return this.fb.group({
      fileOne: this.fb.control(''),
      fileTwo:this.fb.control(''),
      comment: this.fb.control<string>('')
    })
  }

  onFileChange(event:any){
    console.log('>> OnFileChange: ' ,event)
    this.selectedFileOne = event.target.files[0] as File
  }

  upload(){
    const formData = new FormData();
    formData.set('fileOne', this.selectedFileOne);
    formData.set('fileTwo', this.imageFile.nativeElement.files[0])
    formData.set('comment', this.form.value['comment'])

    // DO NOT set Content-Type when sending formdata !!
    // file name of the uploaded file cannot have special character ?
    // const headers = new HttpHeaders().set('Content-Type', 'multipart/form-data').set('Accept','application/json')
    const headers = new HttpHeaders().set('Accept','application/json')

    this.httpClient.post<PostResponse>(`${this.serverUrl}/api/post`,formData, { headers })
    // this.httpClient.post(`${this.serverUrl}/api/post`,formData)
    .subscribe({
      next: v => {
        this.payload = v as PostResponse
        console.log('posted to server')
      }
    })
    
  }

  viewSql(postId:string){
    this.httpClient.get<GetResponse>(`${this.serverUrl}/api/post/sql/${postId}`)
    .subscribe({
      next: v => {
        this.imageDataOne = v['image']
        console.log('get response from server')
      }
    })
  }

  viewMongo(objectId:string){
    this.httpClient.get<GetResponse>(`${this.serverUrl}/api/post/mongo/${objectId}`)
    // this.httpClient.post(`${this.serverUrl}/api/post`,formData)
    .subscribe({
      next: v => {
        this.imageDataTwo = v['image']
        console.log('get response from server')
      }
    })
  }
  
}
