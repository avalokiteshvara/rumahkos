@extends('admin.layout')
@section('content')
	<!-- Content Header (Page header) -->
    <section class="content-header">
      <div class="container-fluid">
        <div class="row mb-2">
          <div class="col-sm-6">
            <h1>Profile</h1>
          </div>
          <div class="col-sm-6">
            <ol class="breadcrumb float-sm-right">
              <li class="breadcrumb-item"><a href="{{ URL::to('/admin') }}">Beranda</a></li>
              <li class="breadcrumb-item active">Profile</li>
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
                <h3 class="card-title">Form Profile</h3>
              </div>
              <!-- /.card-header -->
              <!-- form start -->
             
             {{ Form::open(['url' => 'admin/profile','files' => true]) }}
                <div class="card-body">

                  <div class="form-group">
                    {{ Form::label('nama', 'Nama') }}  
                    {{ Form::text('nama',$data->nama,['class' => 'form-control']) }}
                    @if($errors->has('nama'))
                          <div class="text-danger">
                              {{ $errors->first('nama')}}
                          </div>
                     @endif
                  </div>
                
                  <div class="form-group">
                    {{ Form::label('email', 'Email') }}  
                    {{ Form::text('email',$data->email,['class' => 'form-control','readonly' => 'readonly']) }}
                    @if($errors->has('email'))
                          <div class="text-danger">
                              {{ $errors->first('email')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('telp', 'Nomor Telp') }}  
                    {{ Form::text('telp',$data->telp,['class' => 'form-control']) }}
                    @if($errors->has('telp'))
                          <div class="text-danger">
                              {{ $errors->first('telp')}}
                          </div>
                     @endif
                  </div>

                  <!-- <div class="form-group">
                    {{ Form::label('ktp_nomor', 'Nomor KTP') }}  
                    {{ Form::text('ktp_nomor',$data->ktp_nomor,['class' => 'form-control']) }}
                    @if($errors->has('ktp_nomor'))
                          <div class="text-danger">
                              {{ $errors->first('ktp_nomor')}}
                          </div>
                     @endif
                  </div> -->

                  <!-- <div class="form-group">
                    {{ Form::label('foto', 'Foto Wajah') }}  
                    {{ Form::file('foto', ['class' => 'form-control']) }}
                    @if($errors->has('foto'))
                          <div class="text-danger">
                              {{ $errors->first('foto')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('ktp_foto', 'Foto KTP') }}  
                    {{ Form::file('ktp_foto', ['class' => 'form-control']) }}
                    @if($errors->has('ktp_foto'))
                          <div class="text-danger">
                              {{ $errors->first('ktp_foto')}}
                          </div>
                     @endif
                  </div> -->

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
