@extends('penyewa.layout')
@section('content')
	<!-- Content Header (Page header) -->
    <section class="content-header">
      <div class="container-fluid">
        <div class="row mb-2">
          <div class="col-sm-6">
            <h1>Form Pembayaran</h1>
          </div>
          <div class="col-sm-6">
            <ol class="breadcrumb float-sm-right">
              <li class="breadcrumb-item"><a href="{{ URL::to('/penyewa') }}">Beranda</a></li>     
              <li class="breadcrumb-item"><a href="{{ URL::to('/penyewa/tagihan-sewa') }}">Tagihan Sewa</a></li>         
              <li class="breadcrumb-item active">Form Bayar</li>
            </ol>
          </div>
        </div>
      </div><!-- /.container-fluid -->
    </section>
    <section class="content">
      <div class="container-fluid">
        <div class="row">
          <div class="col-12">
            <!-- general form elements -->
            <div class="card card-primary">
              <div class="card-header">
                <h3 class="card-title">Form Bayar</h3>
              </div>
              <!-- /.card-header -->
              <!-- form start -->
             
             {{ Form::open(['url' => 'penyewa/form-bayar/' . $sewa_id,'files' => true]) }}
                <div class="card-body">
                <div class="alert alert-warning" role="alert">
                    {{ $rekening_bayar }}
                </div>
                 <div class="form-group">
                    {{ Form::label('bank_nama', 'Bank pengirim') }}  
                    {{ Form::text('bank_nama',null,['class' => 'form-control']) }}
                    @if($errors->has('bank_nama'))
                          <div class="text-danger">
                              {{ $errors->first('bank_nama')}}
                          </div>
                     @endif
                  </div>
                
                  <div class="form-group">
                    {{ Form::label('bank_rekening', 'Nomor rekening pengirim') }}  
                    {{ Form::text('bank_rekening',null,['class' => 'form-control']) }}
                    @if($errors->has('bank_rekening'))
                          <div class="text-danger">
                              {{ $errors->first('bank_rekening')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('bank_nama_pengirim', 'Nama pengirim (nama tertera pada bukti transfer)') }}  
                    {{ Form::text('bank_nama_pengirim',null,['class' => 'form-control']) }}
                    @if($errors->has('bank_nama_pengirim'))
                          <div class="text-danger">
                              {{ $errors->first('bank_nama_pengirim')}}
                          </div>
                     @endif
                  </div>

                 
                  <div class="form-group">
                    {{ Form::label('nominal', 'Nominal') }}  
                    {{ Form::text('nominal',$nominal,['class' => 'form-control','readonly' => 'readonly']) }}                    
                  </div>                 

                  <div class="form-group">
                    {{ Form::label('foto', 'Foto Pembayaran') }}  
                    {{ Form::file('foto_pembayaran', ['class' => 'form-control']) }}
                    @if($errors->has('foto_pembayaran'))
                          <div class="text-danger">
                              {{ $errors->first('foto_pembayaran')}}
                          </div>
                     @endif
                  </div>

                  
                  </div>
                </div>

                <div class="card-footer">                  
                  {{ Form::submit('Submit',['class' => 'btn btn-primary']) }}
                </div>
              {{ Form::close() }}  

              
            </div>
            <!-- /.card -->

          </div>
        </div>
      </div>
    </section>
@endsection
