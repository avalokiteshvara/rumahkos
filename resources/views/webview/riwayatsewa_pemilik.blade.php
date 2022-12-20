@extends('webview.layout')
@section('content')

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/rateYo/2.3.2/jquery.rateyo.min.css">
<script src="https://cdnjs.cloudflare.com/ajax/libs/rateYo/2.3.2/jquery.rateyo.min.js"></script>

<style>

.card-body {
    -ms-flex: 1 1 auto;
    flex: 1 1 auto;
    min-height: 1px;
    padding: 0rem;
}

.callout {
  /* padding: 20px; */
  /* margin: 20px 0; */
  border: 1px solid #eee;
  border-left-width: 5px;
  border-radius: 3px;
}
.callout h4 {
  margin-top: 0;
  /* margin-bottom: 5px; */
}
.callout p:last-child {
  margin-bottom: 0;
}
.callout code {
  border-radius: 3px;
}
.callout + .bs-callout {
  margin-top: -5px;
}

.callout-default {
  border-left-color: #777;
}
.callout-default h4 {
  color: #777;
}

.callout-primary {
  border-left-color: #428bca;
}
.callout-primary h4 {
  color: #428bca;
}

.callout-success {
  border-left-color: #5cb85c;
}
.callout-success h4 {
  color: #5cb85c;
}

.callout-danger {
  border-left-color: #d9534f;
}
.callout-danger h4 {
  color: #d9534f;
}

.callout-warning {
  border-left-color: #f0ad4e;
}
.callout-warning h4 {
  color: #f0ad4e;
}

.callout-info {
  border-left-color: #5bc0de;
}
.callout-info h4 {
  color: #5bc0de;
}

.callout-bdc {
  border-left-color: #29527a;
}
.callout-bdc h4 {
  color: #29527a;
}
</style>

<!-- Content Header (Page header) -->
<section class="content" style="padding-top: 10px;">
   <div class="container-fluid">
      <div class="row">
        @if(count($data) > 0)
       
        @else

        <div class="alert alert-info" role="alert" style="display:inline-block;">
          Belum ada data riwayat sewa
        </div>
        
        @endif

        @foreach($data as $d)
        
        <div class="col-12" >
            <div class="card">
                <!-- <img class="card-img-top" src="{{ URL::to('uploads/' . $d->foto) }}" alt="Card image cap"> -->
                <div class="card-body">
                    <h5 class="card-title"></h5>
                    <div class="callout callout-warning">
                        <h4>{{ $d->nama_kos }}</h4>                        
                    </div>

                    <!-- <p class="card-text">Some quick example text to build on the card title and make up the bulk of the card's content.</p> -->
                </div>
                <ul class="list-group list-group-flush">
                    <li class="list-group-item"><b>Penyewa : {{ $d->nama_penyewa }}</b></li>
                    <li class="list-group-item"><b>Sewa Mulai {{ $d->tgl_sewa }} s/d {{ $d->tgl_keluar }}</b></li>
                    
                    <li class="list-group-item"><div id="rateYo{{ $d->id }}"></div></li>
                    <li class="list-group-item"><b>Komentar Penyewa</b> <br/>{{ $d->rating_komentar }}</li>
                </ul>
              
            </div>
        </div>
        <script>
            $(function () {    
                $("#rateYo{{ $d->id }}").rateYo({
                    rating: {{ $d->rating_nilai }},
                    readOnly: true,
                    fullStar: true
                });
            });

            function showRatingDialog(id,rating_nilai,rating_komentar){
               Android.showRatingDialog(id,rating_nilai,rating_komentar);
            }
            
        </script>
        @endforeach                 
         
      </div>
   </div>
</section>

@endsection